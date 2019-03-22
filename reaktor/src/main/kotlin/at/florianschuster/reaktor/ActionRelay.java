package at.florianschuster.reaktor;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Subject that only emits onNext events aka a Relay
 * Reference: https://github.com/JakeWharton/RxRelay/
 */
public final class ActionRelay<T> extends Observable<T> implements Consumer<T> {
    private static final ActionDisposable[] EMPTY = new ActionDisposable[0];
    private final AtomicReference<ActionDisposable<T>[]> subscribers;

    @SuppressWarnings("unchecked")
    public ActionRelay() {
        subscribers = new AtomicReference<ActionDisposable<T>[]>(EMPTY);
    }

    @Override
    public void accept(@NonNull T value) {
        if (value == null) throw new NullPointerException("Value must not be null.");
        ActionDisposable<T>[] currentSubscribers = subscribers.get();
        if (currentSubscribers.length == 0) {
            Exception e = new IllegalStateException("You are not subscribed to 'state' but are trying to publish an 'Action'.");
            Reaktor.INSTANCE.handleError(e);
        } else {
            for (ActionDisposable<T> disposable : currentSubscribers) {
                disposable.onNext(value);
            }
        }
    }

    public int getObserverCount() {
        return subscribers.get().length;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        ActionDisposable<T> actionDisposable = new ActionDisposable<>(observer, this);
        observer.onSubscribe(actionDisposable);
        add(actionDisposable);
        if (actionDisposable.isDisposed()) {
            remove(actionDisposable);
        }
    }

    @SuppressWarnings("unchecked")
    private void add(ActionDisposable<T> disposable) {
        while (true) {
            ActionDisposable<T>[] currentSubscribers = subscribers.get();
            int currentSubscribersLength = currentSubscribers.length;
            ActionDisposable<T>[] newSubscribers = new ActionDisposable[currentSubscribersLength + 1];
            System.arraycopy(currentSubscribers, 0, newSubscribers, 0, currentSubscribersLength);
            newSubscribers[currentSubscribersLength] = disposable;

            if (subscribers.compareAndSet(currentSubscribers, newSubscribers)) {
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void remove(ActionDisposable<T> disposable) {
        while (true) {
            ActionDisposable<T>[] currentSubscribers = subscribers.get();
            if (currentSubscribers == EMPTY) {
                return;
            }

            int currentSubscribersLength = currentSubscribers.length;
            int j = -1;
            for (int i = 0; i < currentSubscribersLength; i++) {
                if (currentSubscribers[i] == disposable) {
                    j = i;
                    break;
                }
            }

            if (j < 0) {
                return;
            }

            ActionDisposable<T>[] newSubscribers;

            if (currentSubscribersLength == 1) {
                newSubscribers = EMPTY;
            } else {
                newSubscribers = new ActionDisposable[currentSubscribersLength - 1];
                System.arraycopy(currentSubscribers, 0, newSubscribers, 0, j);
                System.arraycopy(
                        currentSubscribers,
                        j + 1,
                        newSubscribers,
                        j,
                        currentSubscribersLength - j - 1
                );
            }
            if (subscribers.compareAndSet(currentSubscribers, newSubscribers)) {
                return;
            }
        }
    }

    private static final class ActionDisposable<T> extends AtomicBoolean implements Disposable {
        final Observer<? super T> downstream;
        final ActionRelay<T> parent;

        ActionDisposable(Observer<? super T> actual, ActionRelay<T> parent) {
            this.downstream = actual;
            this.parent = parent;
        }

        void onNext(T t) {
            if (!get()) {
                downstream.onNext(t);
            }
        }

        @Override
        public void dispose() {
            if (compareAndSet(false, true)) {
                parent.remove(this);
            }
        }

        @Override
        public boolean isDisposed() {
            return get();
        }
    }
}