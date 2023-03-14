package la4am12;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

import java.util.Arrays;
import java.util.List;

/**
 * @author : LA4AM12
 * @create : 2023-02-23 09:26:19
 * @description : Mapping cloudlets to Vms using fitness function
 */
public abstract class Scheduler {
	protected List<Cloudlet> cloudletList;
	protected List<Vm> vmList;
	protected int cloudletNum;
	protected int vmNum;

	public Scheduler(List<Cloudlet> cloudletList, List<Vm> vmList) {
		this.cloudletList = cloudletList;
		this.vmList = vmList;
		cloudletNum = cloudletList.size();
		vmNum = vmList.size();
	}

	public abstract int[] allocate();

	public void schedule() {
		int[] cloudletToVm = allocate();
		for (int i = 0; i < cloudletNum; i++) {
			cloudletList.get(i).setVmId(cloudletToVm[i]);
		}
		Log.printLine("time span: " + estimateMakespan(cloudletToVm));
		Log.printLine("LB: " + estimateLB(cloudletToVm));
	}

	public double estimateLB(int[] cloudletToVm) {
		int vmNum = vmList.size();
		int cloudletNum = cloudletList.size();

		double[] executeTimeOfVM = new double[vmNum];
		double avgExecuteTime = 0;
		for (int i = 0; i < cloudletNum; i++) {
			long length = cloudletList.get(i).getCloudletLength();
			int vmId = cloudletToVm[i];
			double	execTime = length / vmList.get(vmId).getMips();
			executeTimeOfVM[vmId] += execTime;
			avgExecuteTime += execTime;
		}
		avgExecuteTime /= vmNum;
		double LB = 0;
		for (int i = 0; i < vmNum; i++) {
			LB += Math.pow(executeTimeOfVM[i] - avgExecuteTime, 2);
		}
		LB = Math.sqrt(LB / vmNum);
		return LB;
	}

	public double estimateMakespan(int[] cloudletToVm) {
		int vmNum = vmList.size();
		int cloudletNum = cloudletList.size();

		double[] executeTimeOfVM = new double[vmNum];
		for (int i = 0; i < cloudletNum; i++) {
			long length = cloudletList.get(i).getCloudletLength();
			int vmId = cloudletToVm[i];
			executeTimeOfVM[vmId] += length / vmList.get(vmId).getMips();
		}
		return Arrays.stream(executeTimeOfVM).max().getAsDouble();
	}
}
