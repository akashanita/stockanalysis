package com.stock.analysis.indicators;

import java.util.List;
import java.util.Map;

import org.patriques.AlphaVantageConnector;
import org.patriques.TechnicalIndicators;
import org.patriques.input.technicalindicators.FastPeriod;
import org.patriques.input.technicalindicators.SeriesType;
import org.patriques.input.technicalindicators.SignalPeriod;
import org.patriques.input.technicalindicators.SlowPeriod;
import org.patriques.input.technicalindicators.TimePeriod;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.technicalindicators.MACD;
import org.patriques.output.technicalindicators.data.MACDData;
import org.patriques.output.timeseries.data.StockData;

import com.stock.anaysis.common.CommonConstants;
import com.stock.anaysis.utils.AudioUtil;
import com.stock.anaysis.utils.CommonUtils;
import com.stock.anaysis.utils.TimeUtils;

public class MACDIndicator {

	double boughtValue = 0.0d;
	public void execute(String stockName, int interval, int slow, int fast) {
		AlphaVantageConnector apiConnector = new AlphaVantageConnector(CommonConstants.API_KEY,
				CommonConstants.TIME_OUT);
		TechnicalIndicators technicalIndicators = new TechnicalIndicators(apiConnector);
		List<StockData> stockData = TimeSeriesExample.execute(stockName, interval);
		try {
			MACD response = technicalIndicators.macd(stockName, CommonUtils.getInterval(interval), TimePeriod.of(10),
					SeriesType.CLOSE, FastPeriod.of(12), SlowPeriod.of(26), SignalPeriod.of(9));
			List<MACDData> data = response.getData();
			if (data.get(0).getHist() > 0.1) {
				System.out.println(stockName + " " + TimeUtils.convertToIndianTime(stockData.get(0).getDateTime()) + " Buy at price: "
						+ stockData.get(0).getClose());
				AudioUtil.notifyBuySignal();
				boughtValue = stockData.get(0).getClose();
			} else if (data.get(0).getHist() < 0 && boughtValue < stockData.get(0).getClose()) {
				System.out.println(stockName + " " + TimeUtils.convertToIndianTime(stockData.get(0).getDateTime())
						+ " Sell at price: " + stockData.get(0).getClose());
				boughtValue = 0.0d;
				AudioUtil.notifySellSignal();
			}
		} catch (AlphaVantageException e) {
			e.printStackTrace();
			System.out.println("something went wrong");
		}
	}
	
	public static void executeForHistory(String stockName, int interval, int slow, int fast) {
		AlphaVantageConnector apiConnector = new AlphaVantageConnector(CommonConstants.API_KEY,
				CommonConstants.TIME_OUT);
		TechnicalIndicators technicalIndicators = new TechnicalIndicators(apiConnector);
		List<StockData> stockData = TimeSeriesExample.execute(stockName, interval);
		try {
			MACD response = technicalIndicators.macd(stockName, CommonUtils.getInterval(interval), TimePeriod.of(10),
					SeriesType.CLOSE, FastPeriod.of(12), SlowPeriod.of(26), SignalPeriod.of(9));
			List<MACDData> data = response.getData();
			boolean buyFlag = false;
			double total = 0;
			double boughtAt = 0;
			double size = (stockData.size() < data.size()) ? stockData.size() : data.size();
			for (int i = ((int) size - 1); i > -1; i--) {
				System.out.println(TimeUtils.convertToIndianTime(stockData.get(i).getDateTime()) + " " + TimeUtils.convertToIndianTime(data.get(i).getDateTime()));
				/*if (data.get(i).getHist() > 0.1 && !buyFlag) {
					System.out.println(stockName + " " + TimeUtils.convertToIndianTime(stockData.get(i).getDateTime()) + " Buy at price: "
							+ stockData.get(i).getClose());
					boughtAt = stockData.get(i).getClose();
					buyFlag = true;
				} else if (data.get(i).getHist() < 0 && buyFlag && boughtAt < stockData.get(i).getClose()) {
					System.out.println(stockName + " " + TimeUtils.convertToIndianTime(stockData.get(i).getDateTime())
							+ " Sell at price: " + stockData.get(i).getClose());
					total += (stockData.get(i).getClose() - boughtAt);
					boughtAt = 0.0d;
					buyFlag = false;
				}*/
			}
			System.out.println("Total: " + total);

		} catch (AlphaVantageException e) {
			e.printStackTrace();
			System.out.println("something went wrong");
		}
	}
}
