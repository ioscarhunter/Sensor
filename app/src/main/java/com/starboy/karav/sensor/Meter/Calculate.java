package com.starboy.karav.sensor.Meter;

public class Calculate {
	private static final float ALPHA = 0.05f;

	static int Partition(double[] a, int p, int r) {
		double x = a[r];

		int i = p - 1;
		double temp;

		for (int j = p; j < r; j++) {
			if (a[j] <= x) {
				i++;
				temp = a[i];
				a[i] = a[j];
				a[j] = temp;
			}
		}

		temp = a[i + 1];
		a[i + 1] = a[r];
		a[r] = temp;
		return (i + 1);
	}

	public static double median(double[] m) {
		int middle = m.length / 2;
		if (m.length % 2 == 1) {
			return m[middle];
		} else {
			return (m[middle - 1] + m[middle]) / 2.0;
		}
	}

	static double findMedian(double[] arr) {
		qSort(arr, 0, arr.length - 1);
		return median(arr);
	}

	public static void qSort(double[] a, int p, int r) {
		if (p < r) {
			int q = Partition(a, p, r);
			qSort(a, p, q - 1);
			qSort(a, q + 1, r);
		}
	}

	static int getPercent(double up, double down, double number) {
		if (number >= up) return 100;
		else if (number <= down) return 0;
		else {
			double toZero = number - down;
			return (int) Math.round(toZero * 300 / (up - down)) / 3;
		}
	}

	public static float[] lowPass(float[] input, float[] output) {
		if (output == null) return input;
		for (int i = 0; i < input.length; i++) {
			output[i] = output[i] + ALPHA * (input[i] - output[i]);
		}
		return output;
	}
}
