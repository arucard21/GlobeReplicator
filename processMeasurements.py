#!/usr/bin/env python3 
import pprint
import csv
import math
import numpy
import scipy.stats
import matplotlib.pyplot as plt

def readMeasurements(filename):
	measurements = {}
	for run in [1,2,3]:
		with open("Evaluation/Run %d/%s" % (run, filename), newline='') as measurementsFile:
			measurementsReader = csv.reader(measurementsFile, skipinitialspace=True)
			# skip header line
			next(measurementsReader)
			for measurementLine in measurementsReader:
				if int(measurementLine[0]) not in measurements:
					measurements[int(measurementLine[0])] = []
				measurements[int(measurementLine[0])].append(int(measurementLine[1]))
	return measurements

# validate the amount of measurements using confidence interval
def validate(header, measurements):
	print(header)
	for objectConfiguration, responseTimes in measurements.items():
		print("For %s replicas:" % objectConfiguration)
		stdDev = scipy.stats.sem(responseTimes)
		print("Sample standard deviation:", stdDev)
		alpha = (1.0-0.95)/2.0
		degreesOfFreedom = len(responseTimes)-1
		tVal = scipy.stats.t.ppf(1-alpha, degreesOfFreedom)
		print("Critical value t-test:", tVal)
		diff = tVal * (stdDev / math.sqrt(len(responseTimes)))
		print()
		mean = numpy.mean(responseTimes)
		print("Mean:", mean)
		print("Confidence interval (mean, manually calculated):")
		print("\t[%f, %f]" % (mean - diff, mean + diff))
		print("Confidence interval (mean, t-test):")
		print("\t[%f, %f]" % scipy.stats.t.interval(alpha, degreesOfFreedom, loc=mean, scale=stdDev))
		print("5% interval mean:")
		print("\t[%f, %f]" % (mean - (0.05*mean), mean + (0.05*mean)))
		print()
		median = numpy.median(responseTimes)
		print("Median (50th percentile):", median)
		print("Confidence interval (median):")
		print("\t[%f, %f]" % (median - diff, median + diff))
		print("Confidence interval (median, t-test):")
		print("\t[%f, %f]" % scipy.stats.t.interval(alpha, degreesOfFreedom, loc=median, scale=stdDev))
		print("5% interval median:")
		print("\t[%f, %f]" % (median - (0.05*median), median + (0.05*median)))
		print()
		tail = numpy.percentile(responseTimes, 99)
		print("Tail (99th percentile):", tail)
		print("Confidence interval (tail):")
		print("\t[%f, %f]" % (tail - diff, tail + diff))
		print("Confidence interval (tail, t-test):")
		print("\t[%f, %f]" % scipy.stats.t.interval(alpha, degreesOfFreedom, loc=tail, scale=stdDev))
		print("5% interval tail:")
		print("\t[%f, %f]" % (tail - (0.05*tail), tail + (0.05*tail)))
		print()

# Plot the measurements in a single image
def plotMeasurements(measurements, filename):
	figure, axes = plt.subplots(ncols=4, sharex=True, sharey=True, figsize=(10.0, 10.0))
	axesIndex = 0
	for objectConfiguration, responseTimes in measurements.items():
		axes[axesIndex].set_title("%s replicas" % objectConfiguration)
		axes[axesIndex].boxplot(responseTimes)
		axes[axesIndex].set_frame_on(False)
		axesIndex += 1
	plt.savefig("Evaluation/%s.png" % filename, transparent=True)

measurementsScalability = readMeasurements("responseTimesScalability.csv")
measurementsConcurrency = readMeasurements("responseTimesConcurrency.csv")

# write all scalability measurements to a file for verification
with open("Evaluation/measurements_scalability.txt", "w") as allMeasurementsFile:
	pprint.pprint(measurementsScalability, allMeasurementsFile, indent=4)
	
# write all concurrency measurements to a file for verification
with open("Evaluation/measurements_concurrency.txt", "w") as allMeasurementsFile:
	pprint.pprint(measurementsConcurrency, allMeasurementsFile, indent=4)

validate("Validating scalability measurements", measurementsScalability)
validate("Validating concurrency measurements", measurementsConcurrency)

plotMeasurements(measurementsScalability, "Response Times Scalability.png")
plotMeasurements(measurementsConcurrency, "Response Times Concurrency.png")
