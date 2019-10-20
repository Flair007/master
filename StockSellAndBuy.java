import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class StockSellAndBuy {
	@SuppressWarnings("serial")
	static class MyException extends Exception 
	{ 
	    public MyException(String s) 
	    { 
	        super(s); 
	    } 
	}
	public static ArrayList<Result> solve(ArrayList<Stock> sellInput,ArrayList<Stock> buyInput,ArrayList<Result> output,HashMap<String,Integer> hm ) {
		int distinctStocks=hm.size();
		ArrayList<ArrayList<Stock>> sell=new  ArrayList<>();
		ArrayList<ArrayList<Stock>> buy=new  ArrayList<>();
		for(int i=0;i<distinctStocks;i++) {
			sell.add(new ArrayList<Stock>());
			buy.add(new ArrayList<Stock>());
			}

		for(Stock stk:sellInput) {
			String stockName=stk.getStock();
			sell.get(hm.get(stockName)).add(stk);
		}
		
		for(Stock stk:buyInput) {	
			String stockName=stk.getStock();
			buy.get(hm.get(stockName)).add(stk);
		}
		for(int i=0;i<distinctStocks;i++) {
			if(sell.get(i).size()==0||buy.get(i).size()==0) {
				continue;
				}
			output=solveHelper(sell.get(i), buy.get(i), output);
		}
		return output;
	}
	
	public static ArrayList<Result> solveHelper(ArrayList<Stock> sellInput,ArrayList<Stock> buyInput,ArrayList<Result> output) {
		PriorityQueue<Stock> pqSell= new PriorityQueue<>(new CustomSellSortingLogic());
		PriorityQueue<Stock> pqBuy= new PriorityQueue<>(new CustomBuySortingLogic());
		int nextSellTime=Integer.MAX_VALUE,nextBuyTime=Integer.MAX_VALUE;
		int totalAvbSell=sellInput.size();
		int totalAvbBuy=buyInput.size();
		int curSellIdx=-1,curBuyIdx=-1;
		Collections.sort(sellInput, new CustomInputSortingLogic());
		Collections.sort(buyInput, new CustomInputSortingLogic());
		while((curSellIdx<totalAvbSell)||(curBuyIdx<totalAvbBuy)) {
			if(curSellIdx+1>=totalAvbSell) {
		    	nextSellTime=Integer.MAX_VALUE;
		    }else {
		    	nextSellTime=sellInput.get(curSellIdx+1).inMinutes();
		    } 
		    if(curBuyIdx+1>=totalAvbBuy) {
		    	nextBuyTime=Integer.MAX_VALUE;
		    }else {
		    	nextBuyTime=buyInput.get(curBuyIdx+1).inMinutes();
		    }
		
		    if(nextBuyTime==Integer.MAX_VALUE&&nextSellTime==Integer.MAX_VALUE) {
		    	break;
		    }
		    if(nextBuyTime==nextSellTime) {
		    	if(buyInput.get(curBuyIdx+1).getOrderId()<sellInput.get(curSellIdx+1).getOrderId()) {
		    		curBuyIdx++;
		    		pqBuy.add(buyInput.get(curBuyIdx));
		    	}else {
		    		curSellIdx++;
		    		pqSell.add(sellInput.get(curSellIdx));
		    	}
		    }else if(nextBuyTime<nextSellTime) {
		    	curBuyIdx++;
	    		pqBuy.add(buyInput.get(curBuyIdx));
		    }else {
		    	curSellIdx++;
	    		pqSell.add(sellInput.get(curSellIdx));
		    }
		    Stock cheapestSellStock,bestBuyer;
		    while(!pqSell.isEmpty()&&!pqBuy.isEmpty()){
		    	 cheapestSellStock=pqSell.poll();
		    	 bestBuyer=pqBuy.poll(); 
		    	 if(cheapestSellStock.getOrderId()<bestBuyer.getOrderId()&&cheapestSellStock.getPrice()<bestBuyer.getPrice()) {
		    	int sellQty=cheapestSellStock.getQuantity(),buyQty= bestBuyer.getQuantity();
		    	int soldQty=0;int diff=buyQty-sellQty;
		    	if(diff>0) {
		    		soldQty=sellQty;
		    		pqBuy.add(new Stock(bestBuyer.getOrderId(), bestBuyer.getTime(),bestBuyer.getStock(), "BUY",diff, bestBuyer.getPrice()));
		    	}else if(diff<0) {
		    		soldQty=buyQty;
		    		pqSell.add(new Stock(cheapestSellStock.getOrderId(), cheapestSellStock.getTime(),cheapestSellStock.getStock(), "SELL",Math.abs(diff), cheapestSellStock.getPrice()));				    		
		    	}else {
		    		soldQty=sellQty;
		    	}
		    	Result res=new Result(cheapestSellStock.getOrderId(),soldQty,cheapestSellStock.getPrice(),bestBuyer.getOrderId());
		    	output.add(res);
		    	}
		    	 else {
		    		pqSell.add(cheapestSellStock);
		    		pqBuy.add(bestBuyer);
		    		break;
		    	}
		    	 if((curSellIdx>=totalAvbSell)&&(curBuyIdx>=totalAvbBuy)) {break;}
		    }
	}
		return output;
	}
	public static void main(String[] args) {
		try {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 					
				int	totOrders = Integer.parseInt(br.readLine());				 
				ArrayList<Result> output=new ArrayList<>();
				ArrayList<Stock> sellInput=new ArrayList<>();
				ArrayList<Stock> buyInput=new ArrayList<>();
				HashMap<String,Integer> hm=new HashMap<>();
				int distinctStocks=0;
				/*
				
				Sample Input Format:
				-----------------------
				
				8
				1 09:45 XAM sell 100 240.10
				2 09:45 XAM sell 90 237.45
				3 09:47 XAM buy 80 238.10
				5 09:48 XAM sell 220 241.50
				6 09:49 XAM buy 50 238.50
				7 09:52 TCS buy 10 1001.10
				8 10:01 XAM sell 20 240.10
				9 10:02 XAM buy 150 242.70
				
				
				Sample Output Format:
				-----------------------
				
				#2 80 237.45 #3
				#2 10 237.45 #6
				#1 100 240.1 #9
				#8 20 240.1 #9
				#5 30 241.5 #9
				
				
				*/
				
				while(totOrders-->0) {
				String ip[]=br.readLine().split(" ");
				if(ip.length!=6) {
					throw new MyException("Invalid Input - Please check");
				}
				int orderId = Integer.parseInt(ip[0]);
				String time=ip[1];
				String stock =ip[2].toUpperCase();
				if(!hm.containsKey(stock)) {
					hm.put(stock,distinctStocks);
					distinctStocks++;
				}
				String transType =ip[3].toUpperCase();
				int quantity = Integer.parseInt(ip[4]);
				double price = Double.parseDouble(ip[5]);
		        Stock stk =new Stock(orderId, time, stock, transType, quantity, price);
		        if(transType.equals("SELL")) {
		        	sellInput.add(stk);
		        }else {
		        	buyInput.add(stk);
		        }
				}
				output=solve(sellInput, buyInput, output, hm);	
				if(output.isEmpty()) {
					System.out.println("\n"+new Result());
				}else {
					Collections.sort(output,new CustomOutputSortingLogic());
					System.out.println();
					for(Result i:output) {
						System.out.print(i);
					}
				}
				} catch (Exception  e) {
						e.printStackTrace();
				}
				}

	static class Stock{
		Integer orderId;
		String time;
		String stock;
		String transType;
		Integer quantity; 
		Double price;
		
		public Stock(Integer orderId, String time, String stock, String transType, Integer quantity, Double price) {
		
			this.orderId = orderId;
			this.time = time;
			this.stock = stock;
			this.transType = transType;
			this.quantity = quantity;
			this.price = price;
		
		}
		public Integer getOrderId() {
			return orderId;
		}
		public void setOrderId(Integer orderId) {
			this.orderId = orderId;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getStock() {
			return stock;
		}
		public void setStock(String stock) {
			this.stock = stock;
		}
		public String getTransType() {
			return transType;
		}
		public void setTransType(String transType) {
			this.transType = transType;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public Double getPrice() {
			return price;
		}
		public void setPrice(Double price) {
			this.price = price;
		}
		
		public String toString() {
			return "Stock [orderId=" + orderId + ", time=" + time + ", stock=" + stock + ", transType=" + transType
					+ ", quantity=" + quantity + ", price=" + price + "]";
		}
		public int inMinutes() {
			String temp[]=this.getTime().split(":");
			int hours=0,minutes=0;
			if(temp.length==2) {
				 hours=Integer.parseInt(temp[0]);
				 minutes=Integer.parseInt(temp[1]);
				minutes+=hours*60;
				if(minutes<0||minutes>1439) {
					try {
						throw new MyException("Check the Entered Time - Valid Range - 00:00 - 23:59 ");
					} 
					catch (MyException e) {
						e.printStackTrace();
						System.exit(0);
					}	
				}
			}else {
				try {
					throw new MyException("Check the Entered Time - Valid Range - 00:00 - 23:59 ");
				} 
				catch (MyException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
			return minutes;
		}
		
	}
	static class Result{
		Integer sellOrderId;
		Integer soldQuantity;
		Double sellPrice;
		Integer buyOrderId;
		public Result() {
			this.sellOrderId = 0;
			this.soldQuantity = 0;
			this.sellPrice = 0.0;
			this.buyOrderId = 0;
		}
		public Result(Integer sellOrderId, Integer soldQuantity, Double sellPrice, Integer buyOrderId) {
			this.sellOrderId = sellOrderId;
			this.soldQuantity = soldQuantity;
			this.sellPrice = sellPrice;
			this.buyOrderId = buyOrderId;
		}
		public Integer getSellOrderId() {
			return sellOrderId;
		}
		public void setSellOrderId(Integer sellOrderId) {
			this.sellOrderId = sellOrderId;
		}
		public Integer getSoldQuantity() {
			return soldQuantity;
		}
		public void setSoldQuantity(Integer soldQuantity) {
			this.soldQuantity = soldQuantity;
		}
		public Double getSellPrice() {
			return sellPrice;
		}
		public void setSellPrice(Double sellPrice) {
			this.sellPrice = sellPrice;
		}
		public Integer getBuyOrderId() {
			return buyOrderId;
		}
		public void setBuyOrderId(Integer buyOrderId) {
			this.buyOrderId = buyOrderId;
		}
		
		public String toString() {
		 	return "#" + sellOrderId + " " + soldQuantity + " " + sellPrice
					+ " #" + buyOrderId+"\n";
		}
	}
	
	static class CustomInputSortingLogic implements Comparator<Stock>{
		public int compare(Stock o1, Stock o2) {
			int timeCompare = o1.inMinutes()-o2.inMinutes(); 
          return (timeCompare!=0) ? timeCompare:(o1.getOrderId()-o2.getOrderId()) ;
		}		
	}
	static class CustomSellSortingLogic implements Comparator<Stock>{
		public int compare(Stock o1, Stock o2) {
			int priceCompare = o1.getPrice().compareTo(o2.getPrice()); 
          return (priceCompare!=0) ? priceCompare:(o1.getOrderId()-o2.getOrderId()) ;
		}		
	}
	static class CustomBuySortingLogic implements Comparator<Stock>{
		public int compare(Stock o1, Stock o2) {
			int priceCompare = (o2.getPrice().compareTo(o1.getPrice())); 
          return (priceCompare!=0) ? priceCompare:(o1.getOrderId()-o2.getOrderId()) ;
		}		
	}
	static class CustomOutputSortingLogic implements Comparator<Result>{
		public int compare(Result r1, Result r2) {
			int buyCompare=r1.getBuyOrderId()-r2.getBuyOrderId();
			double priceCompare=r1.getSellPrice()-r2.getSellPrice();
			return (buyCompare==0)?((priceCompare<0)?-1:1):buyCompare;
		}		
	}
}
