import os
import re
import matplotlib.pyplot as plt

'''
We need to discuss:
- as a function of real time
  - logical clock values (to see jumps)
  - clock drift (defined as difference between
    fastest and slowest logical clocks at each real time)
  - length of message queue
- as a function of tick speed
    - clock jump size statistics
    - message queue statistics

So create the following plots:
- for each round...
  - one graph with a line for each client,
    showing the logical clock value as a function of real time.
    This graph can be used to deduce the clock drift by observing
    the difference between the lines.
  - one graph with a line for each client, showing the length of
    the message queue as a function of real time
'''

'''
Helper functions
'''

# matches all of the integers in `line` using `regex`
def matchInts(regex, line):
  return [int(x) for x in re.findall(regex, line)]

# returns the real time in milliseconds from the Unix Epoch from the given line
def getRealTime(line):
  matched = matchInts(r'(\d+) at logical clock time', line)
  if len(matched) > 0:
     return matched[0]
  return None

# returns the logical clock value from the given line
def getLogicalClockTime(line):
  matched = matchInts(r'at logical clock time (\d+)', line)
  if len(matched) > 0:
     return matched[0]
  return None

# returns the a list of lines corresponding the given client log file
def getLogFromClient(client):
  return open(client, 'r', encoding="utf-8").read().split('\n')

# returns the tick speed of the client given its log by looking at the first line in the log
def getTickSpeedFromLog(log):
  return int(re.findall(r'\d+', log[0])[0])

# returns the message queue length from the given line
def getQueueLength(line):
  matched = matchInts(r'Queue length is (\d+)', line)
  if len(matched) > 0:
    return matched[0]
  return None

'''
Plotting functions
'''

TICK_SPEED_LABEL = 'Tick Speed'

# plots the logical clock value as a function of real time for each client in the given round
def plotLogicialClockValueVsRealTime(path_to_rnd, rnd):
    # returns a list of logical clock values and a list of real times of the same length, with indices in each list corresponding to one another
    def parseOutValues ():
      logical_clock_values = []
      real_times = []
      for line in log[1:]:
        real_time = getRealTime(line)
        logical_clock_time = getLogicalClockTime(line)
        if real_time != None and logical_clock_time != None:
          real_times.append(real_time)
          logical_clock_values.append(logical_clock_time)
      return (real_times, logical_clock_values)
    
    # format, save, and clear the current matplotlib figure
    def prepareFigure():
      plt.xlabel('Real Time (ms, Unix Epoch)')
      plt.ylabel('Logical Clock Value')
      plt.legend()
      plt.title('Logical Clock Value vs Real Time for {}'.format(rnd))
      # save under visualizations
      plt.savefig(os.path.join('.', 'visualizations1', 'logical_clock_values','logical_clock_value_vs_real_time_round_{}.png'.format(rnd)))
      plt.clf()

    # plots a matplotlib figure
    def plot(tick_speed, real_times, logical_clock_values):
      plt.plot(real_times, logical_clock_values, label='{} {}'.format(TICK_SPEED_LABEL, tick_speed))

    for client in os.listdir(path_to_rnd):
      log = getLogFromClient(os.path.join(path_to_rnd, client))
      tick_speed = getTickSpeedFromLog(log)
      real_times, logical_clock_values = parseOutValues()
      plot(tick_speed, real_times, logical_clock_values)

    prepareFigure()

# plots the message queue length as a function of real time for each client in the given round
def plotMessageQueueLengthVsRealTime(path_to_rnd, rnd):
  # parse out the message queue length and real time out of each subsequent log line that is a "received message" line
  def parseOutValues():
    real_times = []
    queue_lengths = []
    for line in log[1:]:
      real_time = getRealTime(line)
      queue_length = getQueueLength(line)
      if queue_length != None and real_time != None:
        queue_lengths.append(queue_length)
        real_times.append(real_time)
    return (real_times, queue_lengths)
  
  # format, save, and clear the current matplotlib figure
  def prepareFigure():
    plt.xlabel('Real Time (ms, Unix Epoch)')
    plt.ylabel('Message Queue Length')
    plt.legend()
    plt.title('Message Queue Length vs Real Time for {}'.format(rnd))
    # save under visualizations
    plt.savefig(os.path.join('.', 'visualizations1', 'message_queue_lengths','message_queue_lengths_vs_real_time_round_{}.png'.format(rnd)))
    plt.clf()

  # plots a matplotlib figure
  def plot (tick_speed, real_times, queue_lengths):
    plt.plot(real_times, queue_lengths,  label='{} {}'.format(TICK_SPEED_LABEL, tick_speed))

  for client in os.listdir(path_to_rnd):
    log = getLogFromClient(os.path.join(path_to_rnd, client))
    tick_speed = getTickSpeedFromLog(log)
    real_times, queue_lengths = parseOutValues()
    plot(tick_speed, real_times, queue_lengths)

  prepareFigure()

'''
Execution
'''

def forEachRound(experiment_path):
    for rnd in os.listdir(experiment_path):
        path_to_rnd = os.path.join(experiment_path, rnd)
        plotLogicialClockValueVsRealTime(path_to_rnd, rnd)
        plotMessageQueueLengthVsRealTime(path_to_rnd, rnd)

def main():
  experiment1 = os.path.join('.', 'experiment1')
  forEachRound(experiment1)

if __name__ == '__main__':
  main()