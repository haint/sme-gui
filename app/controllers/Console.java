/**
 * 
 */
package controllers;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.cloudstack.api.ApiConstants.VMDetails;
import org.sme.tools.cloudstack.AsyncJobAPI;
import org.sme.tools.cloudstack.VirtualMachineAPI;
import org.sme.tools.cloudstack.model.Job;
import org.sme.tools.cloudstack.model.VirtualMachine;
import org.sme.tools.jenkins.JenkinsJob;
import org.sme.tools.jenkins.JenkinsMaster;
import org.sme.tools.knife.Knife;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.console;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 28, 2014
 */
public class Console extends Controller {

  final static Map<String, BlockingQueue<String>> QueueHolder = new HashMap<String, BlockingQueue<String>>();

  public static Result monitor(String git, final int number, final String offering) throws Exception {
    final List<String> slaves = new ArrayList<String>();
    for (int i = 0; i < number; i++) {
      String name = "slave-" + System.currentTimeMillis() + "-" + i;
      slaves.add(name);
      BlockingQueue<String> queue = new ArrayBlockingQueue<String>(20);
      QueueHolder.put(name, queue);
      RunJob thread = new RunJob(name, git, offering);
      thread.start();
    }

    return ok(console.render(slaves));
  }

  public static Result printOut(String name) throws InterruptedException {
    BlockingQueue<String> queue = QueueHolder.get(name);
    if (queue != null) {
      if(queue.size() == 0) return ok(".");
      else if (queue.size() > 1) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < queue.size(); i++) {
          String m = queue.peek();
          if (!"exit".equals(m)) {
            m = queue.take();
            sb.append(m).append("\n");
          } else {
            return status(404);
          }
        }
        return ok(sb.toString());
      }
      String msg = queue.take();
      if ("exit".equals(msg)) return status(404);
      return ok(msg);
    } else {
      return ok(".");
    }
  }

  public static class RunJob extends Thread {

    private final String name;

    private final String offering;
    
    private final String git;

    public RunJob(String name, String git, String offering) {
      this.name = name;
      this.offering = offering;
      this.git = git;
    }

    @Override
    public void run() {
      BlockingQueue<String> queue = Console.QueueHolder.get(name);
      try {
        String response[] = VirtualMachineAPI.quickDeployVirtualMachine(name, "jenkins-slave", offering, "Medium");
        String vmId = response[0];
        String jobId = response[1];
        Job job = AsyncJobAPI.queryAsyncJobResult(jobId);
        while (!job.getStatus().done()) {
          job = AsyncJobAPI.queryAsyncJobResult(jobId);
        }

        if (job.getStatus() == org.apache.cloudstack.jobs.JobInfo.Status.SUCCEEDED) {
          VirtualMachine vm = VirtualMachineAPI.findVMById(vmId, VMDetails.nics);
          queue.poll();
          String ip = vm.nic[0].ipAddress;
          queue.put("Created VM: " + vmId + "\n");
          queue.put("Public IP: " + ip  + "\n");
//          Thread.sleep(10 * 1000);
          Knife knife = Knife.getInstance();
          queue.put("Checking sshd on " + ip);
          while(true) {
            try {
              Socket socket = new Socket(ip, 22);
              socket.close();
              break;
            } catch(Exception e) {
              queue.put(".");
            } 
          }
          if (knife.bootstrap(vm.nic[0].ipAddress, vm.name, queue, "jenkins-slave", "git")) {
//            String ip = "172.27.4.71";
            queue.put(ip);
            JenkinsMaster jkMaster = new JenkinsMaster("git.sme.org", "http", 8080);
            String jobName = "job-" + name;
            JenkinsJob jkJob = new JenkinsJob(jkMaster, jobName, ip, git, "clean install", "");
            int buildNumber = jkMaster.createMavenJobFromGit(jkJob);
            
            Thread.sleep(10 * 1000); //for job submitted
            
            queue.put("Progress: " + jkJob.getName() + "\n");
            while(jkJob.isBuilding(buildNumber)) {
              Thread.sleep(1 * 1000);
              int start = 0;
              byte[] bytes = jkJob.getConsoleOutput(buildNumber, start);
              queue.put(new String(bytes));
              queue.put("\n");
              while (true) {
                Thread.sleep(1 * 1000);
                start += bytes.length;
                int last = bytes.length;
                bytes = jkJob.getConsoleOutput(buildNumber, start);
                byte[] next = new byte[bytes.length - last];
                System.arraycopy(bytes, last, next, 0, next.length);
                if (next.length > 0) { 
                  String output = new String(next);
                  queue.put(output);
                  queue.put("\n");
                  if (output.indexOf("channel stopped") != -1) {
                    queue.put(jkJob.getName() + " finished");
                    queue.put("exit");
                    break;
                  }
                }
              }
            }
            queue.put(jkJob.getName() + " finished");
            queue.put("exit");
          }
        } 
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          queue.put("exit");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
