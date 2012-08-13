/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rex.login;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Sales demo bar chart.
 */
public class SalesStackedBarChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Sales stacked bar chart";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "The monthly sales for the last 2 years (stacked bar chart)";
  }

  class AppIntervals
  {
      String appName;
      List< Boolean > active; // one for every minute of the day
  }
  public void setValues(List< AppIntervals > appIntervals)
  {
      for(int hour = 0; hour < 24; ++hour)
      {
          ArrayList<Integer> cur = new ArrayList<Integer>();
          for(int n = 0; n < appIntervals.size(); ++n)
          {
              cur.add(0);          
          }
          int curNum = 0;
          int curActive = appIntervals.size() - 1;
          for(int minute = 0; minute < 60; ++minute)
          {
              int binNum = hour * 60 + minute;
              AppIntervals curAi = appIntervals.get(curActive);
              if(curAi.active.get(binNum))
              {
                  curNum = minute + 1;
              }
              
              
          }
      }
  }
  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    String[] titles = new String[] { "","", "", "", "","","",""};
    List<double[]> values = new ArrayList<double[]>();
    values.add(new double[] {0,60}); //idle
    values.add(new double[] {0,50});// busy
    values.add(new double[] {60,45});// idle
    values.add(new double[] {40,20});// busy
    values.add(new double[] {40,0});// app
    values.add(new double[] {30,15});// idle
    values.add(new double[] {16,7});    //busy
    values.add(new double[] {8,0});     //idle
//    values.add(new double[] { 1000, 12300, 14240, 15244, 15900, 19200, 22030, 21200, 19500, 15500,
//        12600, 14000 });
//    values.add(new double[] { 5230, 7300, 9240, 10540, 7900, 9200, 12030, 11200, 9500, 10500,
//        11600, 13500 });
    int[] colors = new int[] { Color.BLUE, Color.CYAN, Color.BLUE, Color.CYAN, Color.GREEN, Color.BLUE, Color.CYAN, Color.BLUE };
    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
//  setChartSettings(renderer, "Monthly sales in the last 2 years", "Month", "Units sold", 0.5,
//  12.5, 0, 24000, Color.GRAY, Color.LTGRAY);
    setChartSettings(renderer, "Monthly sales in the last 2 years", "Hour", "Ap", 0,
    2, 0, 60, Color.GRAY, Color.LTGRAY);
    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
    renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
    renderer.getSeriesRendererAt(2).setDisplayChartValues(true);
    renderer.getSeriesRendererAt(3).setDisplayChartValues(true);
    renderer.getSeriesRendererAt(4).setDisplayChartValues(true);
    renderer.getSeriesRendererAt(5).setDisplayChartValues(true);
    renderer.getSeriesRendererAt(6).setDisplayChartValues(true);
    renderer.setXLabels(12);
    renderer.setYLabels(10);
    renderer.setXLabelsAlign(Align.LEFT);
    renderer.setYLabelsAlign(Align.LEFT);
    renderer.setPanEnabled(true, false);
    // renderer.setZoomEnabled(false);
    renderer.setZoomRate(1.1f);
    renderer.setBarSpacing(0.5f);
    return ChartFactory.getBarChartIntent(context, buildBarDataset(titles, values), renderer,
        Type.STACKED);
  }

}
