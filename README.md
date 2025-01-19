Synchronous:
When there is two task 'A' & 'B'.'A' is the first task and be is the next task.
In order to start task 'B' you need to first finish task 'A'.
**Synchronous = blocking (always.)

Asynchronous:
The code you write will be executed sometimes in future.
    - when it will be executed (no idea)
    - How many times it will be executed(no idea)
    List<String> str....
    str.foreach(s->System.out.println(s)
    - how many times:depends on the content of str-> could be 0 or N number of time.
    this lambda inside the for loop is asynchronous code 
    ** Asynchronous is not concurrent programming . above code is asynchronous not concurrent.

Asynchronous+Concurrent:
Running blocking code in an another thread to avoid blocking the the running of main thread.
and this another thread could run 'normally' with a result / run 'exceptionally' with a exception
and need to fetch the result in main thread.

future.get() is blocking code(it blocks the main thread not the calling thread.)
and blocking thread is expensive.