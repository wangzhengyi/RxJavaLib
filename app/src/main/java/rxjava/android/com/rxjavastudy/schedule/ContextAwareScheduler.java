package rxjava.android.com.rxjavastudy.schedule;

import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.NewThreadWorker;
import rx.internal.util.RxThreadFactory;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;


public class ContextAwareScheduler extends Scheduler {
    public static final ContextAwareScheduler INSTANCE = new ContextAwareScheduler();

    final NewThreadWorker worker;

    private ContextAwareScheduler() {
        this.worker = new NewThreadWorker(new RxThreadFactory("ContextAwareScheduler"));
    }

    @Override
    public Worker createWorker() {
        return null;
    }

    static final class ContextAwareWorker extends Worker {
        final CompositeSubscription tracking;
        final NewThreadWorker worker;

        public ContextAwareWorker(NewThreadWorker worker) {
            this.worker = worker;
            this.tracking = new CompositeSubscription();
        }


        @Override
        public Subscription schedule(Action0 action) {
            return schedule(action);
        }

        @Override
        public Subscription schedule(final Action0 action, long delayTime, TimeUnit unit) {
            if (isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            }
            final Object context = ContextManager.get();
            Action0 a = new Action0() {
                @Override
                public void call() {
                    ContextManager.set(context);
                    action.call();
                }
            };

            return worker.scheduleActual(a, delayTime, unit, tracking);
        }

        @Override
        public void unsubscribe() {
            tracking.unsubscribe();
        }

        @Override
        public boolean isUnsubscribed() {
            return tracking.isUnsubscribed();
        }
    }
}
