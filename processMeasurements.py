#!/usr/bin/env python3 
import csv
import math
import numpy
import scipy.stats

with open('Evaluation/Run 1 (test)/responseTimesScalability.csv', newline='') as measurementsFile:
	measurements = csv.reader(measurementsFile, skipinitialspace=True)
	measurements2 = []
	for measurement in measurements:
		if(measurement[0] == '2'):
			measurements2.append(int(measurement[1]))
	
	stdDev = scipy.stats.sem(measurements2)
	print("sample standard deviation:", stdDev)
	tVal = abs(scipy.stats.t.ppf((1.0-0.95)/2.0, 19))
	print("t-value:", tVal)
	diff = tVal * (stdDev / math.sqrt(20))
	print("diff:", diff)
	print()
	mean = numpy.mean(measurements2)
	print("mean:", mean)
	print("confidence interval (mean): [%f, %f]" % (mean - diff, mean + diff))
	print("confidence interval (mean, t-test)", scipy.stats.t.interval(0.025, 19, loc=mean, scale=stdDev))
	
	median = numpy.median(measurements2)
	print("median:", median)
	print("confidence interval (median): [%f, %f]" % (median - diff, median + diff))
	print("confidence interval (median, t-test)", scipy.stats.t.interval(0.025, 19, loc=median, scale=stdDev))
	
	tail = numpy.percentile(measurements2, 99)
	print("tail:", tail)
	print("confidence interval (tail): [%f, %f]" % (tail - diff, tail + diff))
	print("confidence interval (tail, t-test)", scipy.stats.t.interval(0.025, 19, loc=tail, scale=stdDev))
