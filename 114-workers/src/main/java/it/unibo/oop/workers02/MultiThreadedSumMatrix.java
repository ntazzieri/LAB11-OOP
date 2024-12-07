package it.unibo.oop.workers02;

import java.util.List;
import java.util.ArrayList;

/**
 * Multithreaded implementation of SumMatrix.
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThread; 

    /**
     * 
     * @param n no. of thread performing the sum
     */
    public MultiThreadedSumMatrix(final int n) {
        this.nThread = n;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int nElems;
        private final int startPos;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param matrix
         *            the matrix to sum
         * @param startPos
         *            the initial position for this worker
         * @param nElems
         *            the no. of elems to sum up for this worker
         */
        Worker(final double[][] matrix, final int startPos, final int nElems) {
            super();
            this.matrix = matrix;
            this.nElems = nElems;
            this.startPos = startPos;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public void run() {
            System.out.println("Working from position " +  startPos + " to position " + (startPos + nElems - 1));
            final int matrixSize = matrix.length * matrix[0].length;
            for (int i = startPos; i < matrixSize && i < startPos + nElems; i++) {
                final int row = i / matrix.length;
                final int col = i % matrix[0].length;
                this.res = this.res + matrix[row][col];
            }
        }

        /**
         * Returns the result of summing up the doubles within the matrix.
         * 
         * @return the sum of every element in the matrix
         */
        public double getResult() {
            return this.res;
        }

    }


    @Override
    public double sum(final double[][] matrix) {
        final int matrixSize = matrix.length * matrix[0].length;
        final int size = (matrixSize % nThread) + (matrixSize / nThread);
        final List<Worker> workers = new ArrayList<>(nThread);
        for (int start = 0; start < matrixSize; start = start + size) {
            workers.add(new Worker(matrix, start, size));
        }
        workers.forEach(Worker::start);
        double sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum = sum + w.getResult();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }
}
