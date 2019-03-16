package at.florianschuster.githubexample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.florianschuster.reaktor.ReactorView
import at.florianschuster.reaktor.android.ViewModelReactor
import at.florianschuster.reaktor.android.bind
import at.florianschuster.reaktor.android.viewModelReactor
import at.florianschuster.reaktor.changesFrom
import at.florianschuster.reaktor.consume
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import com.jakewharton.rxbinding3.view.visibility
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_github.*
import timber.log.Timber
import java.util.concurrent.TimeUnit


private const val layout: Int = R.layout.fragment_github

class GithubFragment : Fragment(), ReactorView<GithubReactor> {
    override val reactor: GithubReactor by viewModelReactor()
    override val disposables = CompositeDisposable()

    private val adapter = RepoAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvRepos.adapter = adapter
        adapter.onClick = { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url))) }

        bind(reactor)
    }

    override fun bind(reactor: GithubReactor) {
        //action
        searchView.textChanges()
            .skipInitialValue()
            .debounce(300, TimeUnit.MILLISECONDS)
            .map { GithubReactor.Action.UpdateQuery(it.toString()) }
            .consume(reactor)
            .let(disposables::add)

        rvRepos.scrollEvents()
            .sample(500, TimeUnit.MILLISECONDS)
            .filter { it.view.shouldLoadMore() }
            .map { GithubReactor.Action.LoadNextPage }
            .consume(reactor)
            .let(disposables::add)

        //state
        reactor.state.changesFrom { it.repos }
            .bind(adapter::submitList)
            .let(disposables::add)

        reactor.state.changesFrom { it.loadingNextPage }
            .bind(progressLoading.visibility())
            .let(disposables::add)
    }

    private fun RecyclerView.shouldLoadMore(threshold: Int = 8): Boolean {
        val layoutManager: RecyclerView.LayoutManager = layoutManager ?: return false
        return when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition() + threshold > layoutManager.itemCount
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }
}

class GithubReactor(
    initialState: State = State(),
    private val api: GithubApi = GithubApi.create()
) : ViewModelReactor<GithubReactor.Action, GithubReactor.Mutation, GithubReactor.State>(initialState) {
    sealed class Action {
        data class UpdateQuery(val query: String?) : Action()
        object LoadNextPage : Action()
    }

    sealed class Mutation {
        data class SetQuery(val query: String?) : Mutation()
        data class SetRepos(val repos: List<Repo>, val nextPage: Int?) : Mutation()
        data class AppendRepos(val repos: List<Repo>, val nextPage: Int?) : Mutation()
        data class SetLoadingNextPage(val loading: Boolean) : Mutation()
    }

    data class State(
        val query: String? = null,
        val repos: List<Repo> = emptyList(),
        val nextPage: Int? = null,
        val loadingNextPage: Boolean = false
    )

    override fun mutate(action: Action): Observable<out Mutation> = when (action) {
        is Action.UpdateQuery -> {
            Observable.concat(
                Observable.just(Mutation.SetQuery(action.query)),
                Observable.just(Mutation.SetLoadingNextPage(true)),
                search(action.query, 1)
                    .takeUntil(this.action.filter { it is Action.UpdateQuery })
                    .map { Mutation.SetRepos(it.first, it.second) },
                Observable.just(Mutation.SetLoadingNextPage(false))
            )
        }
        is Action.LoadNextPage -> {
            val nextPage = currentState.nextPage
            when {
                currentState.loadingNextPage -> Observable.empty()
                nextPage == null -> Observable.empty()
                else -> {
                    Observable.concat(
                        Observable.just(Mutation.SetLoadingNextPage(true)),
                        search(currentState.query, nextPage)
                            .takeUntil(this.action.filter { it is Action.UpdateQuery })
                            .map { Mutation.AppendRepos(it.first, it.second) },
                        Observable.just(Mutation.SetLoadingNextPage(false))
                    )
                }
            }
        }
    }

    override fun reduce(state: State, mutation: Mutation): State = when (mutation) {
        is Mutation.SetQuery -> state.copy(query = mutation.query)
        is Mutation.SetRepos -> state.copy(repos = mutation.repos, nextPage = mutation.nextPage)
        is Mutation.AppendRepos -> state.copy(repos = state.repos + mutation.repos, nextPage = mutation.nextPage)
        is Mutation.SetLoadingNextPage -> state.copy(loadingNextPage = mutation.loading)
    }

    private fun search(query: String?, page: Int): Observable<Pair<List<Repo>, Int?>> {
        return if (query == null || query.isEmpty()) {
            Observable.empty()
        } else {
            api.repos(query, page)
                .map { it.items }
                .map { it to if (it.isEmpty()) null else page + 1 }
                .doOnError(Timber::e)
                .onErrorReturn { emptyList<Repo>() to null }
                .toObservable()
        }
    }

    override fun transformAction(action: Observable<Action>): Observable<out Action> =
        action.doOnNext { Timber.v("Action: $it") }

    override fun transformState(state: Observable<State>): Observable<out State> =
        state.doOnNext { Timber.v("State: $it") }
}