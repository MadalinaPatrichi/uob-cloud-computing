# Why do we need observation

At this point in the coursework, the "ToDo" application has been successfully developed and migrated from local hosting, to a VM in the cloud, to a containerized service, and finally to an orchestrated Kubernetes Pod. While these changes have given us large improvements in our development flow, deployment and rollback reproducability; we have introduced more and more layers between the app and its hardware host which have made the behaviour of the app more and more opaque to us.

When the app was running locally on our machine, we had immediate access to all of the console output in real time. We also had immediate access to any stacktraces and panics produced if our app had exceptions. We could also see how the app used resources by bringing up the task manager or activity monitor. However if we wanted to work out how many requests our app received or how long it took to process them, we would need to perform some complex log message processing.

Once we moved the application to a VM, we lost almost all access to its internal behaviour. If we wished to access the console output, we had to SSH into the VM and execute the correct commands to extract and view the messages that we cared about. The same went for performance and hardware impact: we needed to execute commands manually while logged in to the machine. If the app crashed, we wouldn't know unless we were looking for it while on the machine.

Moving the app onto Docker and then into a Kubernetes engine removed even our immediate SSH access. `docker` and `kubectl` commands provide you with some commands for viewing logs, events, and resource use (and restrictions) but also add more abstractions between your app and the hardware it runs on.

**It's important to remember that we did these migrations for good reason!** By making our app's deployment and hosting more cloud native we made it cheaper to run, more scalable, and easier to replicate onto more machines. Now it's time to fix the issues regarding logs and measurements.

## Some keywords

- **"Logs"** : A message with a timestamp coming from a source. The message can be "structured" which could add various other information such as process ID, request ID, log level, etc. 
    
    - `2018-01-01T00:00:00 Happy New Year!`

- **"Metric"** : A consistent and repeated measurement made against an application or as the result of some measurement process at a particular point in time.

    - At `2018-01-01T00:00:00` the app was using 12 Megabytes of memory
    - Between `2018-01-01T00:00:00` and `2018-01-01T00:00:05` the app received 42 http requests
    - A request took `12ms` to process
    - Over some period the app used `N%` of cpu cycles
