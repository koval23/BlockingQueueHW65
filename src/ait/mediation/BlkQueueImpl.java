package ait.mediation;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlkQueueImpl<T> implements BlkQueue<T> {
    int maxSize;
    List<T> blkList;
    Lock mutex = new ReentrantLock();
    Condition senderWaitingCondition = mutex.newCondition();
    Condition receiverWaitingCondition = mutex.newCondition();

    public BlkQueueImpl(int maxSize) {
        // TODO
        this.maxSize = maxSize;
        blkList = new LinkedList<>();
    }

    // ------------------------------------------------------------------------
    // todo push -> Сообщение
    @Override
    public void push(T message) {
        // TODO
        mutex.lock();
        try {
            while (blkList.size()== maxSize) {
                try {
                    senderWaitingCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            blkList.add(message);
            receiverWaitingCondition.signal();
        } finally {
            mutex.unlock();
        }
    }

    // todo pop -> get
    @Override
    public T pop() {
        // TODO
        mutex.lock();
        try {
            while (blkList.isEmpty()) {
                try {
                    receiverWaitingCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            T message = blkList.remove(0);
            senderWaitingCondition.signal();
            return message;
        } finally {
            mutex.unlock();
        }
    }
}
