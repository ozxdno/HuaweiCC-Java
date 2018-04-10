package com.elasticcloudservice.predict;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Predict {

	public static String[] predictVm(String[] ecsContent, String[] inputContent) {

		/** =========do your work here========== **/
		
		Data.input = new Input();
		Data.log = new Log();
		Data.esc = new ESC();
		Data.output = new Output();
		
		Data.fit0 = new Fit0();
		Data.allocate0 = new Allocate0();
		
		Data.input.readContentFrom(inputContent);
		Data.esc.readContentFrom(ecsContent);
		
		Data.vmreq  = new int[Data.input.getVMTypeAmount()+1][Data.length];
		Data.vmsum  = new int[Data.input.getVMTypeAmount()+1][Data.length];
		//Data.cpureq = new int[Data.input.getVMTypeAmount()+1][Data.length];
		//Data.memreq = new int[Data.input.getVMTypeAmount()+1][Data.length];
		
		Data.predict = new int[Data.input.getVMTypeAmount()];
		Data.allocates = new ArrayList<Model_Distribution>();
		
		Data.esc.sort(Data.input.getEndTime() - Data.input.getBeginTime());
		Data.esc.sum();
		
		Data.fit0.setVMReq(Data.vmreq[0]);
		Data.fit0.setVMSum(Data.vmsum[0]);
		Data.fit0.setAmount(Data.amount);
		Data.fit0.fit();
		Data.predict_vmsum = (int)Data.fit0.getPredict();
		
		for(int i=0; i<Data.input.getVMTypeAmount(); i++) {
			Data.fit0.setVMReq(Data.vmreq[i+1]);
			Data.fit0.setVMSum(Data.vmsum[i+1]);
			Data.fit0.setAmount(5);
			
			int testId = i + 1;
			if(testId == 2 || testId == 6 || testId == 7 || testId == 8) {
				i = i - 1 + 1;
			}
			
			if(Data.fit0.fit()) {
				Data.predict[i] = (int)Data.fit0.getPredict();
			} else {
				Data.predict[i] = 0;
			}
		}
		
		/* DEBUG
		for(int i=0; i<Data.predict.length; i++) {
			Data.predict[i] = 10;
		}
		*/
		
		Data.allocate0.sum();
		Data.allocate0.setUseMethod_1(true);
		Data.allocate0.setUseMethod_2(true);
		Data.allocate0.setNeedAdjust(true);
		Data.allocate0.allocate();
		Data.allocate0.evaluate();
		
		
		System.out.println(Data.score1);
		System.out.println(Data.score2);
		return Data.output.saveContentTo();
		
		/** =============== end ================ **/
	}

}

/**
 * 日志数据模型，记录各种错误、提示信息。
 * @author ozxdno
 *
 */
class Model_Log {
	private String time;
	private int level;
	private String type;
	private String message;
	
	public void setLevel(int level) {
		this.level = level;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getTime() {
		return time;
	}
	public int getLevel() {
		return level;
	}
	public String getType() {
		return type;
	}
	public String getMessage() {
		return message;
	}
	
	public Model_Log() {
		initThis();
	}
	/**
	 * 创建一条 Log
	 * @param type Log 类型
	 * @param message 附加信息
	 * @param level 0-提示信息；1-警告信息；2-普通错误；3-严重错误（程序无法继续执行或结果有错）；
	 */
	public Model_Log(String type, String message, int level) {
		initThis();
		setLevel(level);
		setType(type);
		setMessage(message);
	}
	private void initThis() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		time = sdf.format(d);
		level = 0;
		type = "";
		message = "";
	}
}

/**
 * 各种数据；类的实例；
 * @author ozxdno
 *
 */
class Data {
	public static Input input;
	public static Log log;
	public static ESC esc;
	public static Output output;
	
	public static Fit0 fit0;
	public static Allocate0 allocate0;
	
	public static int[][] vmreq;
	public static int[][] vmsum;
	//public static int[][] cpureq;
	//public static int[][] memreq;
	public static int amount;
	public final static int length = 10;
	
	public static int predict_vmsum;
	public static int[] predict;
	public static List<Model_Distribution> allocates;
	
	public static double score1;
	public static double score2;
}

/**
 * 日志集合
 * @author ozxdno
 *
 */
class Log {
	private List<Model_Log> content = new ArrayList<Model_Log>();
	
	public void add(Model_Log log) {
		if(log == null) {
			return;
		}
		content.add(log);
	}
}

/**
 * 虚拟机参数模型
 * @author ozxdno
 *
 */
class Model_VM {
	private String type;
	private int id;
	private int cpu;
	private int memory;
	
	public void setType(String type) {
		this.type = type;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setCpu(int cpu) {
		this.cpu = cpu;
	}
	public void setMemory(int memory) {
		this.memory = memory;
	}
	
	public String getType() {
		return type;
	}
	public int getId() {
		return id;
	}
	public int getCpu() {
		return cpu;
	}
	public int getMemory() {
		return memory;
	}
	
	public Model_VM() {
		initThis();
	}
	private void initThis() {
		type = "";
		id = -1;
		cpu = 0;
		memory = 0;
	}
}

/**
 * ESC 训练数据的数据模型
 * @author ozxdno
 *
 */
class Model_ESCData {
	private String type;
	private int id;
	private long time;
	private String date;
	
	public void setType(String type) {
		this.type = type;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getType() {
		return type;
	}
	public int getId() {
		return id;
	}
	public long getTime() {
		return time;
	}
	public String getDate() {
		return date;
	}
	
	public Model_ESCData() {
		initThis();
	}
	private void initThis() {
		type = "";
		id = -1;
		time = 0;
	}
}

/**
 * 预测虚拟机需求后，虚拟机在服务器上的分布模型
 * @author ozxdno
 *
 */
class Model_Distribution {
	private int id;
	private int[] amount;
	private int remainCpu;
	private int remainMem;
	
	public void setId(int id) {
		this.id = id;
	}
	public void setAmount(int amount, int vmId) {
		this.amount[vmId] = amount;
		int cpu = Data.input.getVMTypes().get(vmId).getCpu();
		int mem = Data.input.getVMTypes().get(vmId).getMemory();
		this.remainCpu += this.amount[vmId]*cpu;
		this.remainMem += this.amount[vmId]*mem;
		this.remainCpu -= amount*cpu;
		this.remainMem -= amount*mem;
	}
	
	public int getId() {
		return id;
	}
	public int getAmount(int vmId) {
		return this.amount[vmId];
	}
	public int getRemain() {
		if(Data.input.getOptType() == 1) {
			return this.remainCpu;
		}
		if(Data.input.getOptType() == 2) {
			return this.remainMem;
		}
		return 0;
	}
	public int getRemainCpu() {
		return this.remainCpu;
	}
	public int getRemainMemory() {
		return this.remainMem;
	}
	
	public Model_Distribution() {
		initThis();
	}
	private void initThis() {
		id = -1;
		if(amount == null) {
			amount = new int[Data.input.getVMTypeAmount()];
		}
		this.remainCpu = Data.input.getServerCpu();
		this.remainMem = Data.input.getServerMem() * 1024;
	}
	
	public void add(int vmId) {
		amount[vmId]++;
		this.remainCpu -= Data.input.getVMTypes().get(vmId).getCpu();
		this.remainMem -= Data.input.getVMTypes().get(vmId).getMemory();
	}
	public void remove(int vmId) {
		amount[vmId]--;
		this.remainCpu += Data.input.getVMTypes().get(vmId).getCpu();
		this.remainMem += Data.input.getVMTypes().get(vmId).getMemory();
	}
	public boolean isEnough(int vmId) {
		if(remainCpu < Data.input.getVMTypes().get(vmId).getCpu()) {
			return false;
		}
		if(remainMem < Data.input.getVMTypes().get(vmId).getMemory()) {
			return false;
		}
		return true;
	}
}

/**
 * 输入文件的相关操作和数据信息
 * @author ozxdno
 *
 */
class Input {
	private int serverCpu;
	private int serverMem;
	private long serverRom;
	private int vmTypeAmount;
	private List<Model_VM> vmTypes;
	/**
	 * 优化类型：
	 * 0 - 未指定；
	 * 1 - CPU；
	 * 2 - MEM；
	 */
	private int optType;
	private long beginTime;
	private long endTime;
	
	public int getServerCpu() {
		return this.serverCpu;
	}
	public int getServerMem() {
		return this.serverMem;
	}
	public long getServerRom() {
		return this.serverRom;
	}
	public int getVMTypeAmount() {
		return this.vmTypeAmount;
	}
	public List<Model_VM> getVMTypes() {
		return vmTypes;
	}
	public int getOptType() {
		return optType;
	}
	public long getBeginTime() {
		return this.beginTime;
	}
	public long getEndTime() {
		return this.endTime;
	}
	
	
	public Input() {
		initThis();
	}
	private void initThis() {
		serverCpu = 0;
		serverMem = 0;
		serverRom = 0;
		vmTypeAmount = 0;
		if(vmTypes == null) {
			vmTypes = new ArrayList<Model_VM>();
		}
		vmTypes.clear();
		optType = 0;
		beginTime = 0;
		endTime = 0;
	}
	
	
	public boolean readContentFrom(final String[] content) {
		initThis();
		int part = 0;
		int partStartRow = 0;
		for(int row=0; row < content.length; ++row) {
			String line = content[row];
			if(part == 0) {
				if(line.isEmpty()) {
					continue;
				} else {
					String[] items = line.split("[ \\t]");
					if(items.length != 3) {
						Data.log.add(new Model_Log("Input File","at row: " + String.valueOf(row),3));
						return false;
					}
					try {
						this.serverCpu = Integer.parseInt(items[0]);
						this.serverMem = Integer.parseInt(items[1]);
						this.serverRom = Long.parseLong(items[2]);
						part++;
						partStartRow = row;
						continue;
					} catch(Exception e) {
						Data.log.add(new Model_Log("Input File","at row: " + String.valueOf(row),3));
						return false;
					}
				}
			}
			if(part == 1) {
				if(!line.isEmpty()) {
					Data.log.add(new Model_Log("Input File","at row: " + String.valueOf(row),3));
					return false;
				}
				part++;
				partStartRow = row + 1;
				continue;
			}
			if(part == 2) {
				if(row - partStartRow == 0) {
					try {
						this.vmTypeAmount = Integer.parseInt(line);
						continue;
					} catch(Exception e) {
						Data.log.add(new Model_Log("Input File","at row: " + String.valueOf(row),3));
						return false;
					}
				}
				if(line.isEmpty()) {
					//this.sortVMTypes();
					part++;
					partStartRow = row + 1;
					continue;
				}
				
				String[] items = line.split("[ \\t]");
				if(items.length != 3) {
					Data.log.add(new Model_Log("Input File","at row: " + String.valueOf(row),3));
					return false;
				}
				try {
					Model_VM vm = new Model_VM();
					vm.setType(items[0]);
					vm.setCpu(Integer.parseInt(items[1]));
					vm.setMemory(Integer.parseInt(items[2]));
					//vm.setId(this.vmTypes.size());
					this.vmTypes.add(vm);
					continue;
				} catch(Exception e) {
					Data.log.add(new Model_Log("Input File","at row: " + String.valueOf(row),3));
					return false;
				}
			}
			if(part == 3) {
				if(row - partStartRow == 0) {
					if(line.equals("CPU")) {
						this.optType = 1;
						continue;
					}
					if(line.equals("MEM")) {
						this.optType = 2;
						continue;
					}
					Data.log.add(new Model_Log("Input File","at row: " + String.valueOf(row),3));
					return false;
				}
				if(line.isEmpty()) {
					this.sortVMTypes();
					part = part + 1;
					partStartRow = row + 1;
					continue;
				}
				Data.log.add(new Model_Log("Input File","at row: " + String.valueOf(row),3));
				return false;
			}
			if(part == 4) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(row - partStartRow == 0) {
					try {
						this.beginTime = sdf.parse(line).getTime()/1000;
						continue;
					} catch(Exception e) {
						Data.log.add(new Model_Log("Input File","at row: " + String.valueOf(row),3));
						return false;
					}
				}
				if(row - partStartRow == 1) {
					try {
						this.endTime = sdf.parse(line).getTime()/1000;
						continue;
					} catch(Exception e) {
						Data.log.add(new Model_Log("Input File","at row: " + String.valueOf(row),3));
						return false;
					}
				}
				break;
			}
		}
		
		if(this.vmTypeAmount != this.vmTypes.size()) {
			Data.log.add(new Model_Log("Input File","VM Type Amount Error",3));
			return false;
		}
		
		return true;
	}
	public int IndexOf(String vmtype) {
		if(vmtype == null) {
			return -1;
		}
		for(int i=0; i < this.vmTypeAmount; i++) {
			if(vmtype.equals(vmTypes.get(i).getType())) {
				return i;
			}
		}
		return -1;
	}
	public void sortVMTypes() {
		if(this.optType == 1) {
			for(int i=1; i<this.vmTypeAmount; i++) {
				for(int j= 0; j<this.vmTypeAmount-i; j++) {
					if(this.vmTypes.get(j).getCpu() > this.vmTypes.get(j+1).getCpu()) {
						Model_VM temp = this.vmTypes.get(j);
						this.vmTypes.set(j, this.vmTypes.get(j+1));
						this.vmTypes.set(j+1, temp);
					}
				}
			}
		}
		if(this.optType == 2) {
			for(int i=1; i<this.vmTypeAmount; i++) {
				for(int j= 0; j<this.vmTypeAmount-i; j++) {
					if(this.vmTypes.get(j).getMemory() > this.vmTypes.get(j+1).getMemory()) {
						Model_VM temp = this.vmTypes.get(j);
						this.vmTypes.set(j, this.vmTypes.get(j+1));
						this.vmTypes.set(j+1, temp);
					}
				}
			}
		}
		
		for(int i=0; i<this.vmTypeAmount; i++) {
			this.vmTypes.get(i).setId(i);
		}
	}
}

/**
 * ESC 训练数据（历史需求）的文件信息
 * @author ozxdno
 *
 */
class ESC {
	private List<Model_ESCData> escData;
	
	public ESC() {
		initThis();
	}
	private void initThis() {
		if(escData == null) {
			escData = new ArrayList<Model_ESCData>();
		}
		escData.clear();
	}
	
	public boolean readContentFrom(final String[] content) {
		initThis();
		for(int row = 0; row < content.length; row++) {
			String line = content[row];
			String[] items = line.split("[ \\t]");
			if(items.length != 4) {
				Data.log.add(new Model_Log("ESC Data File","at: " + String.valueOf(row),3));
				return false;
			}
			try {
				Model_ESCData esc = new Model_ESCData();
				esc.setType(items[1]);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				esc.setDate(items[2] + " " + items[3]);
				esc.setTime(sdf.parse(esc.getDate()).getTime() / 1000 );
				esc.setId(Data.input.IndexOf(esc.getType()));
				if(esc.getId() != -1) {
					escData.add(esc);
				}
				continue;
			} catch(Exception e) {
				Data.log.add(new Model_Log("ESC Data File","at: " + String.valueOf(row),3));
				return false;
			}
		}
		return true;
	}
	/**
	 * 对训练用数据按时间跨度进行分类
	 * @param step 时间跨度，单位：s
	 */
	public void sort(long step) {
		Data.amount = 0;
		if(escData == null || escData.size() == 0) {
			return;
		}
		for(Model_ESCData e : escData) {
			int group = (int)((Data.input.getBeginTime() - e.getTime()) / step);
			if(group < Data.length) {
				//int cpu = Data.input.getVMTypes().get(e.getId()).getCpu();
				//int mem = Data.input.getVMTypes().get(e.getId()).getMemory();
				
				Data.vmreq[0][group]++;
				Data.vmreq[e.getId()+1][group]++;
				//Data.cpureq[0][group] += cpu;
				//Data.cpureq[e.getId()+1][group] += cpu;
				//Data.memreq[0][group] += mem;
				//Data.memreq[e.getId()+1][group] += mem;
				
				Data.vmsum[0][group]++;
				Data.vmsum[e.getId()+1][group]++;
				
				if(group + 1 > Data.amount) {
					Data.amount = group + 1;
				}
			} else {
				Data.log.add(
						new Model_Log("tip in sorting: wrong data",
								"time: " + String.valueOf(e.getTime()),0));
			}
		}
		
	}
	public void sum() {
		for(int i=Data.amount-1; i>0; i--) {
			for(int j=0; j<=Data.input.getVMTypeAmount(); j++) {
				//Data.vmreq[j][i-1] += Data.vmreq[j][i];
				//Data.cpureq[j][i-1] += Data.cpureq[j][i];
				//Data.memreq[j][i-1] += Data.memreq[j][i];
				
				Data.vmsum[j][i-1] += Data.vmsum[j][i];
			}
		}
	}
}

/**
 * 输出
 * @author ozxdno
 *
 */
class Output {
	
	public String[] saveContentTo() {
		int rows = 3 + Data.input.getVMTypeAmount() + Data.allocates.size();
		String[] result = new String[rows];
		
		int sumVMAmount = 0;
		for(int amount : Data.predict) {
			sumVMAmount += amount;
		}
		result[0] = String.valueOf(sumVMAmount);
		//result[1] = "";
		
		for(int i=0; i<Data.input.getVMTypeAmount(); i++) {
			result[1+i] = Data.input.getVMTypes().get(i).getType() + " " +
					String.valueOf(Data.predict[i]);
		}
		result[1+Data.input.getVMTypeAmount()] = "";
		
		result[1+Data.input.getVMTypeAmount() + 1] = String.valueOf(Data.allocates.size());
		for(int i=0; i<Data.allocates.size(); i++) {
			String allocate = "";
			for(int j=0; j<Data.input.getVMTypeAmount(); j++) {
				int n = Data.allocates.get(i).getAmount(j);
				if(n > 0) {
					allocate += Data.input.getVMTypes().get(j).getType() + " " +
							String.valueOf(n) + " ";
				}
			}
			if(allocate.length() > 0) {
				allocate = allocate.substring(0, allocate.length()-1);
			}
			result[3+Data.input.getVMTypeAmount() + i] = String.valueOf(i+1) + " " + allocate;
		}
		return result;
	}
}

/**
 * Fit0 为预测拟合的总方法；Fit1 到 FitN 为各种子类方法；总方法统一子类方法结果；
 * @author ozxdno
 *
 */
class Fit0 {
	private int[] vmreq;
	private int[] vmsum;
	private int amount = 0;
	
	private double a2;
	private double a1;
	private double a0;
	
	private int sumAmount;
	private int sumCpu;
	private int sumMemory;
	
	public void setVMReq(int[] vmreq) {
		this.vmreq = vmreq;
	}
	public void setVMSum(int[] vmsum) {
		this.vmsum = vmsum;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public int[] getVMReq() {
		return this.vmreq;
	}
	public int[] getVMSum() {
		return this.vmsum;
	}
	public int getAmount() {
		return this.amount;
	}
	public double getA2() {
		return a2;
	}
	public double getA1() {
		return a1;
	}
	public double getA0() {
		return a0;
	}
	public int getSumPredictAmount() {
		return this.sumAmount;
	}
	public int getSumPredictCpu() {
		return this.sumCpu;
	}
	public int getSumPredictMemory() {
		return this.sumMemory;
	}
	
	public Fit0() {
		initThis();
	}
	private void initThis() {
		a2 = 0;
		a1 = 0;
		a0 = 0;
		sumAmount = 0;
		sumCpu = 0;
		sumMemory = 0;
	}
	
	public boolean fit() {
		
		//Fit3 fit3 = new Fit3();
		//fit3.fit();
		
		return true;
		
		/*
		if(amount > 2) {
			int sum = vmreq[1] + vmreq[0];
			int cnt = 2;
			for(int i=2; i<amount; i++) {
				int err = vmreq[i] - sum/cnt;
				if(err < 0) {
					err = -err;
				}
				if(err > sum/cnt) {
					amount = i;
					break;
				}
				sum += vmreq[i];
				cnt++;
			}
		}
		
		Fit2 fit2 = new Fit2();
		fit2.fit();
		a0 = fit2.getA0();
		a1 = fit2.getA1();
		a2 = fit2.getA2();
		return true;
		*/
	}
	public double getPredict() {
		
		Fit4 f4 = new Fit4();
		f4.fit();
		return f4.getPredict();
		
		/*
		Fit3 f3 = new Fit3();
		return f3.fit();
		*/
		
		/*
		double pre = a2 + a1 + a0;
		if(pre < 0) {
			Data.log.add(new Model_Log("Wrong Predict Result",String.valueOf(pre),1));
		}
		double res = pre - this.vmsum[0];
		if(res < 0) {
			res = 0;
		}
		return res;
		*/
	}
	public int sumPredictAmount() {
		int sum = 0;
		for(int n : Data.predict) {
			sum += n;
		}
		sumAmount = sum;
		return sum;
	}
	public int sumPredictCpu() {
		int sum = 0;
		for(int i=0; i<Data.input.getVMTypeAmount(); i++) {
			if(Data.predict[i] > 0) {
				int cpu = Data.input.getVMTypes().get(i).getCpu();
				sum += cpu*Data.predict[i];
			}
		}
		sumCpu = sum;
		return sum;
	}
	public int sumPredictMemory() {
		int sum = 0;
		for(int i=0; i<Data.input.getVMTypeAmount(); i++) {
			if(Data.predict[i] > 0) {
				int mem = Data.input.getVMTypes().get(i).getMemory();
				sum += mem*Data.predict[i];
			}
		}
		sumMemory = sum;
		return sum;
	}
	public void compare(String testResultFile) {
		
	}
}

class Fit1 {
	private double a1;
	private double a0;
	
	public double getA1() {
		return a1;
	}
	public double getA0() {
		return a0;
	}
	
	public Fit1() {
		initThis();
	}
	private void initThis() {
		a1 = 0;
		a0 = 0;
	}
	
	public boolean fit() {
		int[] dots = Data.fit0.getVMSum();
		int amount = Data.fit0.getAmount();
		int nrem = 0;
		
		if(dots == null || dots.length < 2 || amount < 2) {
			return false;
		}
		double sumX = 0;
		double sumY = 0;
		double sumXX = 0;
		double sumXY = 0;
		for(int i=0; i<amount; i++) {
			if(dots[i] < 0) {
				nrem++;
				continue;
			}
			sumX += -i;
			sumY += dots[i];
			sumXX += i*i;
			sumXY += -i*dots[i];
		}
		
		amount -= nrem;
		
		double den = amount*sumXX - sumX*sumX;
		if(den == 0) {
			Data.log.add(new Model_Log("Fit1","den = 0",2));
			return false;
		}
		
		a0 = (sumY*sumXX - sumX*sumXY) / den;
		a1 = (amount*sumXY - sumX*sumY) / den;
		return true;
	}
	public int predict() {
		double pre = a0 + a1;
		if(pre < 0) {
			pre = 0;
		}
		return (int)pre;
	}
}

class Fit2 {
	private double a2;
	private double a1;
	private double a0;
	
	public double getA2() {
		return a2;
	}
	public double getA1() {
		return a1;
	}
	public double getA0() {
		return a0;
	}
	
	public Fit2() {
		initThis();
	}
	private void initThis() {
		a2 = 0;
		a1 = 0;
		a0 = 0;
	}
	
	public boolean fit() {
		int[] dots = Data.fit0.getVMSum();
		int amount = Data.fit0.getAmount();
		int nrem = 0;
		
		if(dots == null || dots.length < 3 || amount < 3) {
			return false;
		}
		double sumX = 0;
		double sumX2 = 0;
		double sumX3 = 0;
		double sumX4 = 0;
		double sumY = 0;
		double sumXY = 0;
		double sumX2Y = 0;
		for(int i=0; i<amount; i++) {
			if(dots[i] < 0) {
				nrem++;
				continue;
			}
			double x = -i;
			double x2 = x*x;
			double x3 = x2*x;
			double x4 = x3*x;
			double y = dots[i];
			double xy = x*y;
			double x2y = xy*x;
			
			sumX += x;
			sumX2 += x2;
			sumX3 += x3;
			sumX4 += x4;
			sumY += y;
			sumXY += xy;
			sumX2Y += x2y;
		}
		
		amount -= nrem;
		
		double den =
				amount*sumX2*sumX4 + 
				sumX*sumX3*sumX2*2 -
				amount*sumX3*sumX3 - 
				sumX*sumX*sumX4 -
				sumX2*sumX2*sumX2;
		if(den == 0) {
			Data.log.add(new Model_Log("Fit2","den = 0",2));
			return false;
		}
		
		a0 = sumY*sumX2*sumX4 + sumXY*sumX2*sumX3 + sumX2Y*sumX*sumX3 -
				sumY*sumX3*sumX3 - sumXY*sumX*sumX4 - sumX2Y*sumX2*sumX2;
		a1 = sumY*sumX3*sumX2 + sumXY*amount*sumX4 + sumX2Y*sumX2*sumX -
				sumY*sumX*sumX4 - sumXY*sumX2*sumX2 - sumX2Y*amount*sumX3;
		a2 = sumY*sumX*sumX3 + sumXY*sumX*sumX2 + sumX2Y*amount*sumX2 -
				sumY*sumX2*sumX2 - sumXY*amount*sumX3 - sumX2Y*sumX*sumX;
		
		a0 = a0 / den;
		a1 = a1 / den;
		a2 = a2 / den;
		
		return true;
	}
	public int predict() {
		double pre = a0 + a1 + a2;
		if(pre < 0) {
			pre = 0;
		}
		return (int)pre;
	}
}

class Fit3 {
	private int dde;
	private int de;
	private int e;
	
	private double p_dde;
	private double p_de;
	private double p_e;
	
	public Fit3() {
		initThis();
	}
	private void initThis() {
		dde = 0;
		de = 0;
		e = 0;
		
		p_dde = 0.04;
		p_de = 0.2;
		p_e = 0.8;
	}
	
	public int fit() {
		
		if(Data.fit0.getAmount() > 2) {
			e = Data.fit0.getVMSum()[0] - Data.fit0.getVMSum()[1];
		}
		if(Data.fit0.getAmount() > 3) {
			int e1 = e;
			int e2 = Data.fit0.getVMSum()[1] - Data.fit0.getVMSum()[2];
			de = e1 - e2;
		}
		if(Data.fit0.getAmount() > 4) {
			int de1 = de;
			int de2 = Data.fit0.getVMSum()[3] + Data.fit0.getVMSum()[1] - 2*Data.fit0.getVMSum()[2];
			dde = de1 - de2;
		}
		
		double pre =
				(double)p_e*e +
				(double)p_de*de + 
				(double)p_dde*dde;
		
		if(pre < 0) {
			pre = 0;
		}
		
		return (int)pre;
	}
}

class Fit4 {
	
	private int[] newReqs;
	private int amount;
	private int predict;
	private int uPeriod;
	private int dPeriod;
	private int uPeak;
	private int uBottom;
	private int dPeak;
	private int dBottom;
	/**
	 * 00 - 未定义；
	 * 01 - V；
	 * 02 - 倒 V；
	 */
	private int shape;
	
	public int getPredict() {
		return this.predict;
	}
	
	public Fit4() {
		initThis();
	}
	private void initThis() {
		newReqs = new int[Data.amount];
		amount = 0;
		predict = 0;
		uPeriod = 0;
		dPeriod = 0;
		uPeak = 0;
		dPeak = 0;
		uBottom = 0;
		dBottom = 0;
		shape = 0;
	}
	
	public boolean fit() {
		
		// remove same element
		amount = 0;
		for(int i=0; i<Data.amount-1; i++) {
			if(Data.fit0.getVMReq()[i] != Data.fit0.getVMReq()[i+1]) {
				newReqs[amount] = Data.fit0.getVMReq()[i];
				amount++;
			}
		}
		
		// init
		this.uPeriod = 0;
		this.dPeriod = 0;
		this.uPeak = 0;
		this.dPeak = 0;
		this.uBottom = 0;
		this.dBottom = 0;
		
		// amount not enough
		if(amount < 2) {
			this.predict = Data.fit0.getVMReq()[0];
			return true;
		}
		
		// get tendency, include up info and down info
		int t1 = this.newReqs[0] - this.newReqs[1];
		int t2 = -t1;
		boolean arriveT2 = false;
		for(int i=0; i<amount-1; i++) {
			int t = newReqs[i] - newReqs[i+1];
			if(t > 0) {
				if(arriveT2 && t1 > 0) {
					break;
				}
				if(this.uPeriod == 0) {
					this.uPeak = newReqs[i];
				}
				this.uBottom = newReqs[i+1];
				this.uPeriod++;
				if(t2 > 0) {
					arriveT2 = true;
				}
			}
			if(t < 0) {
				if(arriveT2 && t1 < 0) {
					break;
				}
				if(this.dPeriod == 0) {
					this.dBottom = newReqs[i];
				}
				this.dPeak = newReqs[i+1];
				this.dPeriod++;
				if(t2 < 0) {
					arriveT2 = true;
				}
			}
		}
		
		// data not enough
		if(!arriveT2) {
			this.predict = Data.fit0.getVMReq()[0];
			return true;
		}
		
		// get shape
		if(t1 > 0) {
			shape = 1;
		}
		if(t1 < 0) {
			shape = 2;
		}
		
		// v
		if(shape == 1) {
			int den = this.dPeak - this.dBottom;
			if(den == 0) {
				den = 1;
			}
			double backRate = (double)(this.dPeak - this.uPeak) / den;
			//int dp = this.uPeriod - this.dPeriod;
			if(backRate < 0.2) {
				if(this.uPeriod == 0) {
					this.uPeriod = 1;
				}
				double downRate = (double)(this.uPeak - this.uBottom) / (double)this.uPeriod;
				this.predict = Data.fit0.getVMReq()[0] - (int)downRate;
			} else {
				int uperiod = this.dPeriod - this.uPeriod;
				if(uperiod <= 0) {
					uperiod = 1;
				}
				
				int lastPeak = (int)(this.dPeak * 1.05);
				double upRate = (double)(lastPeak - Data.fit0.getVMReq()[0]) / uperiod;
				this.predict = Data.fit0.getVMReq()[0] + (int)upRate;
			}
		}
		// ^
		if(shape == 2) {
			int den = this.uPeak - this.uBottom;
			if(den == 0) {
				den = 1;
			}
			double backRate = (double)(this.dBottom - this.uBottom) / den;
			//int dp = this.dPeriod - this.uPeriod;
			if(backRate < 0.2) {
				if(this.dPeriod == 0) {
					this.dPeriod = 1;
				}
				double upRate = (double)(this.dPeak - this.dBottom) / this.dPeriod;
				this.predict = Data.fit0.getVMReq()[0] + (int)upRate;
			} else {
				int dperiod = this.uPeriod - this.dPeriod;
				if(dperiod <= 0) {
					dperiod = 1;
				}
				int lastBottom = (int)(this.uBottom * 1.05);
				double downRate = (double)(Data.fit0.getVMReq()[0] - lastBottom) / dperiod;
				this.predict = Data.fit0.getVMReq()[0] - (int)downRate;
			}
		}
		
		return true;
	}
}

/**
 * 把虚拟机分配到服务器的方法；
 * @author ozxdno
 *
 */
class Allocate0 {
	private int minServer;
	private boolean use1;
	private boolean use2;
	private boolean needAdjust;
	
	public int getMinServer() {
		return this.minServer;
	}
	
	public void setUseMethod_1(boolean use) {
		use1 = use;
	}
	public void setUseMethod_2(boolean use) {
		use2 = use;
	}
	public void setNeedAdjust(boolean need) {
		this.needAdjust = need;
	}
	
	public Allocate0() {
		initThis();
	}
	private void initThis() {
		minServer = 0;
		use1 = true;
		use2 = true;
		needAdjust = true;
	}
	
	public boolean allocate() {
		
		if(use2) {
			Allocate2 a2 = new Allocate2();
			boolean ok = a2.allocate();
			Data.allocates = a2.getAllocates();
			if(ok) {
				for(int i=a2.getAllocates().size()-1; i>=0; i--) {
					Model_Distribution d = a2.getAllocates().get(i);
					if(d.getRemain() > 0 && !this.adjust(d)) {
						a2.getAllocates().remove(d);
					}
				}
				this.refreshPredict();
				return true;
			}
		}
		
		if(use1) {
			Allocate1 a1 = new Allocate1();
			a1.allocate();
			Data.allocates = a1.getAllocates();
			for(int i=Data.allocates.size()-1; i>=0; i--) {
				Model_Distribution d = Data.allocates.get(i);
				if(d.getRemain() > 0 && !this.adjust(d)) {
					Data.allocates.remove(d);
				}
			}
			this.refreshPredict();
			return true;
		}
		
		return true;
	}
	public void sum() {
		Data.fit0.sumPredictAmount();
		Data.fit0.sumPredictCpu();
		Data.fit0.sumPredictMemory();
		
		int minServerForCpu = Data.fit0.getSumPredictCpu() / Data.input.getServerCpu() + 1;
		int minServerForMemory = Data.fit0.getSumPredictMemory() / (Data.input.getServerMem()*1024) + 1;
		minServer = minServerForCpu < minServerForMemory ?
				minServerForCpu :
				minServerForMemory;
	}
	public void evaluate() {
		
		List<Model_Distribution> allocates = Data.allocates;
		
		if(Data.input.getOptType() == 1) {
			int sumRemain = 0;
			for(Model_Distribution d : allocates) {
				sumRemain += d.getRemainCpu();
			}
			Data.score2 = 1 - (double)sumRemain / (double)(allocates.size()*Data.input.getServerCpu());
		}
		if(Data.input.getOptType() == 2) {
			int sumRemain = 0;
			for(Model_Distribution d : allocates) {
				sumRemain += d.getRemainMemory();
			}
			Data.score2 = 1 - (double)sumRemain / (double)(allocates.size()*Data.input.getServerMem()*1024);
		}
	}
	public void fill(Model_Distribution d) {
		for(int i=Data.input.getVMTypeAmount()-1; i>=0; i--) {
			while(d.isEnough(i)) {
				d.add(i);
			}
		}
	}
	public boolean adjust(Model_Distribution d) {
		
		if(!this.needAdjust) {
			return true;
		}
		
		int maxCpu = Data.input.getVMTypes().get(Data.input.getVMTypeAmount()-1).getCpu();
		int maxMem = Data.input.getVMTypes().get(Data.input.getVMTypeAmount()-1).getMemory();
		int max = Data.input.getOptType() == 1 ? maxCpu : (Data.input.getOptType() == 2 ? maxMem : 0);
		if(d.getRemain() > max) {
			int r = Data.input.getOptType() == 1 ? Data.input.getServerCpu() / d.getRemainCpu() :
				( Data.input.getOptType() == 2 ? Data.input.getServerMem() / d.getRemainMemory() : 100 );
			int sd = 0;
			for(int i=0; i<Data.input.getVMTypeAmount(); i++) {
				sd += d.getAmount(i);
			}
			int sp = 0;
			for(int i : Data.predict) {
				sp += i;
			}
			int s = sp / sd;
			
			if(r <= 2 && s >= 20) {
				return false;
			}
			fill(d);
		}
		
		int bestFill = 0;
		int bestId1 = 0;
		int bestId2 = 0;
		
		for(int i=0; i<Data.input.getVMTypeAmount(); i++) {
			if( d.getRemain() == bestFill ) {
				break;
			}
			if(d.getAmount(i) == 0) {
				continue;
			}
			for(int j=0; j<Data.input.getVMTypeAmount(); j++) {
				if(i == j) {
					continue;
				}
				
				int addCpu = Data.input.getVMTypes().get(j).getCpu();
				int addMem = Data.input.getVMTypes().get(j).getMemory();
				int delCpu = Data.input.getVMTypes().get(i).getCpu();
				int delMem = Data.input.getVMTypes().get(i).getMemory();
				
				if(Data.input.getOptType() == 1) {
					if(addCpu <= delCpu) {
						continue;
					}
				}
				if(Data.input.getOptType() == 2) {
					if(addMem <= delMem) {
						continue;
					}
				}
				
				int adjCpu = addCpu - delCpu;
				int adjMem = addMem - delMem;
				
				if( adjCpu > d.getRemainCpu() ) {
					continue;
				}
				if( adjMem > d.getRemainMemory() ) {
					continue;
				}
				
				if( Data.input.getOptType() == 1 ) {
					if( adjCpu > bestFill ) {
						bestFill = adjCpu;
						bestId1 = i;
						bestId2 = j;
					}
				}
				if( Data.input.getOptType() == 2 ) {
					if( adjMem > bestFill ) {
						bestFill = adjMem;
						bestId1 = i;
						bestId2 = j;
					}
				}
			}
		}
		
		d.remove(bestId1);
		d.add(bestId2);
		return true;
	}
	public void refreshPredict() {
		for(int i=0; i<Data.predict.length; i++) {
			Data.predict[i] = 0;
		}
		for(Model_Distribution d : Data.allocates) {
			for(int i=0; i<Data.input.getVMTypeAmount(); i++) {
				Data.predict[i] += d.getAmount(i);
			}
		}
	}
}

class Allocate1 {
	private List<Model_Distribution> allocates;
	
	public List<Model_Distribution> getAllocates() {
		return this.allocates;
	}
	
	public Allocate1() {
		initThis();
	}
	private void initThis() {
		if(allocates == null) {
			allocates = new ArrayList<Model_Distribution>();
		}
		allocates.clear();
	}
	
	public boolean allocate() {
		this.allocates.clear();
		
		for(int i=Data.input.getVMTypeAmount()-1; i>=0; i--) {
			for(int j=0; j<Data.predict[i]; j++) {
				if(this.allocates.size() == 0 || !this.allocates.get(this.allocates.size()-1).isEnough(i)) {
					Model_Distribution d = new Model_Distribution();
					d.add(i);
					this.allocates.add(d);
					continue;
				}
				for(int k=0; k<this.allocates.size(); k++) {
					Model_Distribution d = this.allocates.get(k);
					if(d.isEnough(i)) {
						d.add(i);
						break;
					}
				}
			}
		}
		
		return true;
	}
}

class Allocate2 {
	private List<Model_Distribution> allocates;
	
	public List<Model_Distribution> getAllocates() {
		return this.allocates;
	}
	
	public Allocate2() {
		initThis();
	}
	private void initThis() {
		if(allocates == null) {
			this.allocates = new ArrayList<Model_Distribution>();
		}
		allocates.clear();
	}
	
	public boolean allocate() {
		int MAX_ARRAY_SIZE = 10000;
		int bagContent = 0;
		int baseMem = 1024; //Data.input.getVMTypes().get(0).getMemory();
		if(Data.input.getOptType() == 1) {
			bagContent = Data.input.getServerCpu();
		}
		if(Data.input.getOptType() == 2) {
			bagContent = Data.input.getServerMem() * 1024 / baseMem;
			/*
			if(bagContent * 1024 != Data.input.getServerMem()) {
				return false;
			}
			*/
			for(int i=1; i<Data.input.getVMTypeAmount(); i++) {
				int n = Data.input.getVMTypes().get(i).getMemory() % baseMem;
				if(n != 0) {
					return false;
				}
			}
		}
		if(bagContent > MAX_ARRAY_SIZE) {
			return false;
		}
		
		
		int[] distribution;
		int[] remain = Data.predict.clone();
		while(true) {
			int sumRemain = 0;
			for(int n : remain) {
				sumRemain += n;
			}
			if(sumRemain == 0) {
				break;
			}
			
			//  DEBUG
			
			
			
			distribution = bagSolution( remain,bagContent );
			Model_Distribution d = new Model_Distribution();
			for(int i=0; i<distribution.length; i++) {
				for(int j=0; j<distribution[i]; j++) {
					d.add(i);
				}
			}
			allocates.add(d);
			
			for(int i=0; i<remain.length; i++) {
				remain[i] -= distribution[i];
			}
		}
		
		
		return true;
	}
	
	private int[] bagSolution(int[] vmAmount, int bagContent) {
		
		int sum = 0;
		for(int i : vmAmount) {
			sum += i;
		}
		
		int[][][] distribution = new int[sum][bagContent+1][2];
		int index = 0;
		int baseMem = 1024; //Data.input.getVMTypes().get(0).getMemory();
		int serverCpu = Data.input.getServerCpu();
		int serverMem = Data.input.getServerMem() * 1024 / baseMem;
		boolean done = false;
		int index0 = sum-1;
		int index1 = bagContent;
		
		for(int i=0; i<=bagContent && !done; i++) {
			if(i == 17) {
				// DEBUG
				index = 0;
			}
			index = 0;
			for(int j=vmAmount.length-1; j>=0 && !done; j--) {
				for(int k=0; k<vmAmount[j] && !done; k++) {
					
					if(index == 74) {
						// DEBUG
						index = 74;
					}
					
					int cpu = Data.input.getVMTypes().get(j).getCpu();
					int mem =  Data.input.getVMTypes().get(j).getMemory() / baseMem;
					
					if(Data.input.getOptType() == 1) {
						int v1 = index >= 1 ? distribution[index-1][i][0] : 0;
						int v2 = 0;
						int v21 = (index >= 1 && i >= cpu) ? distribution[index-1][i-cpu][0] : 0;
						int v22 = (index >= 1 && i >= cpu) ? distribution[index-1][i-cpu][1] : 0;
						if(v21 + cpu <= i && v22 + mem <= serverMem) {
							v2 = v21 + cpu;
						}
						if( v2 > v1 ) {
							distribution[index][i][0] = v21 + cpu;
							distribution[index][i][1] = v22 + mem;
						} else {
							distribution[index][i][0] = index >= 1 ? distribution[index-1][i][0] : 0;
							distribution[index][i][1] = index >= 1 ? distribution[index-1][i][1] : 0;
						}
						
						done = distribution[index][i][0] == serverCpu;
						if(done) {
							index0 = index;
							index1 = i;
						}
						index++;
					}
					if(Data.input.getOptType() == 2) {
						int v1 = index >= 1 ? distribution[index-1][i][1] : 0;
						int v2 = 0;
						int v21 = (index >= 1 && i >= mem) ? distribution[index-1][i-mem][0] : 0;
						int v22 = (index >= 1 && i >= mem) ? distribution[index-1][i-mem][1] : 0;
						if(v21 + cpu <= serverCpu && v22 + mem <= i) {
							v2 = v22 + mem;
						} else {
							// DEBUG
							v2 = 0;
						}
						if( v2 > v1 ) {
							distribution[index][i][0] = v21 + cpu;
							distribution[index][i][1] = v22 + mem;
						} else {
							distribution[index][i][0] = index >= 1 ? distribution[index-1][i][0] : 0;
							distribution[index][i][1] = index >= 1 ? distribution[index-1][i][1] : 0;
						}
						
						done = distribution[index][i][1] == serverMem;
						if(done) {
							index0 = index;
							index1 = i;
						}
						index++;
					}
				}
			}
		}
		
		int[] select = new int[vmAmount.length];
		for(int i=0; i<select.length; i++) {
			select[i] = 0;
		}
		
		int opt = Data.input.getOptType() - 1;
		int content = distribution[index0][index1][opt];
		while(index0 >=0 && index1 >= 0) {
			if(index0 == 0 && content == 0) {
				break;
			}
			if(index0 >= 1 && distribution[index0][index1][opt] == distribution[index0-1][index1][opt]) {
				index0--;
				continue;
			}
			int bg = 0;
			int ed = 0;
			int s = 0;
			for(int i=vmAmount.length-1; i>=0; i--) {
				bg = ed;
				ed = bg + vmAmount[i];
				if(bg <= index0 && index0 < ed) {
					select[i]++;
					s = i;
					break;
				}
			}
			
			int cpu = Data.input.getVMTypes().get(s).getCpu();
			int mem = Data.input.getVMTypes().get(s).getMemory() / baseMem;
			if(Data.input.getOptType() == 1) {
				index0 -= 1;
				index1 -= cpu;
				content -= cpu;
			}
			if(Data.input.getOptType() == 2) {
				index0 -= 1;
				index1 -= mem;
				content -= mem;
			}
		}
		
		return select;
	}
}
