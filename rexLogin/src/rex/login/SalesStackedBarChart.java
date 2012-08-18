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
import java.util.LinkedList;
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
  class PriorityList
  {
      int binNum;
      int index; // The highest priority app which is active
  }
  String[] titles = null;
  LinkedList<double[]> values = null;
  int[] colors = null;
  public boolean setValues(List< AppIntervals > appIntervals, int numColumns, int binsPerColumn)
  {
      int numApps = appIntervals.size();
      int maxColSize = 0;
      ArrayList< ArrayList<PriorityList> > pCollection = new ArrayList< ArrayList<PriorityList> >();
      for(int hour = 0; hour < numColumns; ++hour)
      {
          ArrayList<PriorityList> pList = new ArrayList<PriorityList>();
          pCollection.add(pList);
          int curActive = 0;
          // Find out where we start
          for(int n = appIntervals.size() - 1; n >= 0; --n)
          {
              List<Boolean> curAi = appIntervals.get(n).active;
              if(curAi.get(hour*binsPerColumn))
              {
                  curActive = n + 1;
                  break;
              }
          }
          PriorityList p = new PriorityList();
          for(int minute = 0; minute < binsPerColumn; ++minute)
          {
              int binNum = hour * binsPerColumn + minute;
              int lastActive = curActive;
              curActive = 0;
              for(int n = appIntervals.size() - 1; n >= 0; --n)
              {
                  List<Boolean> curAi = appIntervals.get(n).active;
                  if(curAi.get(binNum))
                  {
                      curActive = n + 1;
                      break;
                  }
              }
              if(curActive != lastActive)
              {
                  p = new PriorityList();
                  p.binNum = minute;
                  p.index = lastActive;
                  pList.add(p);
              }
          }
          p = new PriorityList();
          p.binNum = binsPerColumn;
          p.index = curActive;
          pList.add(p);
          
          if(pList.size() > maxColSize)
              maxColSize = pList.size();
      }
      ++maxColSize;
      titles = new String[maxColSize * (numApps + 1)];
      values = new LinkedList<double[]>();
      colors = new int[maxColSize * (numApps + 1)];
      for(int n = 0; n < maxColSize; ++n)
      {
          for(int m = 0; m <= numApps; ++m)
          {
              int index = n * (numApps + 1) + m;
              if(n == 0)
              {
                  if(m == numApps)
                      titles[index] = "Idle";
                  else
                      titles[index] = appIntervals.get(numApps - m - 1).appName;
              }
              else
                  titles[index] = "";
              if(m == 2)
                  colors[index] = Color.BLUE;
              else if (m == 1)
                  colors[index] = Color.CYAN;
              else if (m == 0)
                  colors[index] = Color.GREEN;
          }
      }
      boolean allDone = false;
      int curIndex = 0;
      while(!allDone)
      {
          allDone = true;
          for(int appNum = 0; appNum <= numApps; ++appNum)
          {
              double[] row = new double[numColumns];
              for(int colNum = 0; colNum < numColumns; ++colNum)
              {
                  int curAppNum = 0;
                  int curHeight = 0;
                  ArrayList<PriorityList> pList = pCollection.get(colNum);
                  if(curIndex < pList.size())
                  {
                      PriorityList p = pList.get(curIndex);
                      curAppNum = p.index;
                      curHeight = p.binNum;
                      allDone = false;
                  }
                  if(appNum == curAppNum)
                      row[colNum] = curHeight;
                  else
                      row[colNum] = 0;
              }
              values.addFirst(row);
          }
          ++curIndex;
      }
      return true;
  }
  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
      // debug code
      List< AppIntervals > appIntervals = new ArrayList<AppIntervals>();
      AppIntervals ai = new AppIntervals();
      ai.active = new ArrayList<Boolean>();
      ai.appName = "AllApps";
      for(int n = 0; n < 3; ++n)
          ai.active.add(true);
      for(int n = 3; n < 5; ++n)
          ai.active.add(false);
      for(int n = 5; n < 9; ++n)
          ai.active.add(true);
      ai.active.add(false);
      appIntervals.add(ai);

      ai = new AppIntervals();
      ai.active = new ArrayList<Boolean>();
      ai.appName = "AppRex";
      for(int n = 0; n < 3; ++n)
          ai.active.add(true);
      for(int n = 3; n < 8; ++n)
          ai.active.add(false);
      ai.active.add(true);
      ai.active.add(false);
      appIntervals.add(ai);
      
      //this.setValues(appIntervals, 2, 5);
      
      
      
/*      
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
*/
    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
//  setChartSettings(renderer, "Monthly sales in the last 2 years", "Month", "Units sold", 0.5,
//  12.5, 0, 24000, Color.GRAY, Color.LTGRAY);
    setChartSettings(renderer, "Monthly sales in the last 2 years", "Hour", "Ap", 0,
    24, 0, 60, Color.GRAY, Color.LTGRAY);
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
