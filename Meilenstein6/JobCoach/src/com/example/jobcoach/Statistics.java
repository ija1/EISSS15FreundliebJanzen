package com.example.jobcoach;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.widget.TableRow.LayoutParams;

/**
 * Activity for calculate the ratings and display on table
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class Statistics extends Activity {
	
	private int sumGoodEmployee = 0;
	private int sumCentralEmployee = 0;
	private int sumBadEmployee = 0;
	private int sumGoodAutist = 0;
	private int sumCentralutist = 0;
	private int sumBadAutist = 0;
	
	
	private double percentGoodEmployee = 0;
	private double percentCentralEmployee = 0;
	private double percentBadEmployee = 0;
	private double percentGoodAutist = 0;
	private double percentCentralutist = 0;
	private double percentBadAutist = 0;
	private DecimalFormat precision = new DecimalFormat("#0.0");
	
	private TableLayout.LayoutParams tableRowParams;
	

	/**
     * Method to display statistic
     * 
     * @param savedInstanceState save last Instance state
	 *
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistic);
		
		tableRowParams=new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

		if (Profile.fileExistance("statistik.json", getApplicationContext())) {
					List<Situation> situationlist = Profile.getSituationList(getApplicationContext(), "statistik.json");
					if (!situationlist.isEmpty()) {
						
						// calculate all ratings for one Kategorie and put in StatisticTabel List,
						// before calculate next categories look into StatisticTabel List so that calculation happens not doubletime
						List<StatisticTabel> StatisticList = new ArrayList<StatisticTabel>();
						boolean calculationFlag=false;
						for (int i=0;i<situationlist.size();i++) {		
								if (!StatisticList.isEmpty()) {
									for (int z=0;z<StatisticList.size();z++) {
										if ( situationlist.get(i).getKategorie().getName().equals(StatisticList.get(z).getCategorie())) {
											calculationFlag=true;
										}
									}
								}
								if (!calculationFlag) {
									StatisticTabel tabel = new StatisticTabel();
									tabel.setCategorie(situationlist.get(i).getKategorie().getName());
									
									if (situationlist.get(i).getNotfallhandlung().isGoodRatingFromEmployee()) {
										tabel.setCountGoodEmployee(tabel.getCountGoodEmployee()+1);
									}
									if (situationlist.get(i).getNotfallhandlung().isCentralRatingFromEmployee()) {
										tabel.setCountCentralEmployee(tabel.getCountCentralEmployee()+1);
									}
									if (situationlist.get(i).getNotfallhandlung().isBadRatingFromEmployee()) {
										tabel.setCountBadEmployee(tabel.getCountBadEmployee()+1);
									}
									
									
									if (situationlist.get(i).getNotfallhandlung().isGoodRatingFromAutist()) {
										tabel.setCountGoodAutist(tabel.getCountGoodAutist()+1);
									}
									if (situationlist.get(i).getNotfallhandlung().isCentralRatingFromAutist()) {
										tabel.setCountCentralutist(tabel.getCountCentralutist()+1);
									}
									if (situationlist.get(i).getNotfallhandlung().isBadRatingFromAutist()) {
										tabel.setCountBadAutist(tabel.getCountBadAutist()+1);
									}
									for (int j=i+1;j<situationlist.size();j++) {
										if (situationlist.get(i).getKategorie().getName().equals(situationlist.get(j).getKategorie().getName())) {
										   	
											if (situationlist.get(j).getNotfallhandlung().isGoodRatingFromEmployee()) {
												tabel.setCountGoodEmployee(tabel.getCountGoodEmployee()+1);
											}
											if (situationlist.get(j).getNotfallhandlung().isCentralRatingFromEmployee()) {
												tabel.setCountCentralEmployee(tabel.getCountCentralEmployee()+1);
											}
											if (situationlist.get(j).getNotfallhandlung().isBadRatingFromEmployee()) {
												tabel.setCountBadEmployee(tabel.getCountBadEmployee()+1);
											}
	
											
											if (situationlist.get(j).getNotfallhandlung().isGoodRatingFromAutist()) {
												tabel.setCountGoodAutist(tabel.getCountGoodAutist()+1);
											}
											if (situationlist.get(j).getNotfallhandlung().isCentralRatingFromAutist()) {
												tabel.setCountCentralutist(tabel.getCountCentralutist()+1);
											}
											if (situationlist.get(j).getNotfallhandlung().isBadRatingFromAutist()) {
												tabel.setCountBadAutist(tabel.getCountBadAutist()+1);
											}
										}
									}
									StatisticList.add(tabel);
								
								}
								calculationFlag = false;
						}
	
						if(!StatisticList.isEmpty()) {
							
							for (StatisticTabel tabel : StatisticList) {
								TableRow statisticData = new TableRow(this);
								tableRowParams.setMargins(10, 0, 0, 0);
								statisticData.setLayoutParams(tableRowParams);
										
								LayoutParams rowParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								rowParams.setMargins(5, 0, 0, 0);
								LayoutParams rowParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								LayoutParams rowCentral = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								rowCentral.setMargins(15, 0, 0, 0);
										
								TextView categorie = new TextView(this);
								categorie.setWidth(81);
								categorie.setTextSize(11);
								categorie.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
								categorie.setLayoutParams(rowParams2);
		
								ImageView border1 = new ImageView(this);
								LayoutParams imageParams =new LayoutParams(2, LayoutParams.FILL_PARENT);
								border1.setLayoutParams(imageParams);
								border1.setBackgroundColor(Color.parseColor("#FF909090"));
										
								TextView goodRatingEmployee = new TextView(this);
								goodRatingEmployee.setWidth(55);
								goodRatingEmployee.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
								goodRatingEmployee.setLayoutParams(rowParams);
								goodRatingEmployee.setTextSize(11);
								goodRatingEmployee.setLayoutParams(rowParams);
										
								TextView centralRatingEmployee = new TextView(this);
								centralRatingEmployee.setWidth(95);
								centralRatingEmployee.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
								centralRatingEmployee.setTextSize(11);
								centralRatingEmployee.setLayoutParams(rowCentral);
									
								TextView badRatingEmployee = new TextView(this);
								badRatingEmployee.setWidth(50);
								badRatingEmployee.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
								badRatingEmployee.setTextSize(11);
								badRatingEmployee.setLayoutParams(rowParams);
		
								ImageView border2 = new ImageView(this);
								LayoutParams imageParams2 =new LayoutParams(2, LayoutParams.FILL_PARENT);
								border2.setLayoutParams(imageParams2);
								border2.setBackgroundColor(Color.parseColor("#FF909090"));
										
								TextView goodRatingAutist = new TextView(this);
								goodRatingAutist.setWidth(45);
								goodRatingAutist.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
								goodRatingAutist.setTextSize(11);
								goodRatingAutist.setLayoutParams(rowParams2);
		
								TextView centralRatingAutist = new TextView(this);
								centralRatingAutist.setWidth(60);
								centralRatingAutist.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
								centralRatingAutist.setTextSize(11);
								centralRatingAutist.setLayoutParams(rowParams);
		
								TextView badRatingAutist = new TextView(this);
								badRatingAutist.setWidth(75);
								badRatingAutist.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
								badRatingAutist.setTextSize(11);
								badRatingAutist.setLayoutParams(rowParams);
										
								View v = new View(this);
								LayoutParams viewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 2);
								v.setLayoutParams(viewParams);
								v.setBackgroundColor(Color.parseColor("#FF909090"));
	
								categorie.setText(tabel.getCategorie());
								
								if (tabel.getCountGoodEmployee() != 0) {
									goodRatingEmployee.setText(Integer.toString(tabel.getCountGoodEmployee()));
									sumGoodEmployee += tabel.getCountGoodEmployee();
								}
								if (tabel.getCountCentralEmployee() != 0) {
									centralRatingEmployee.setText(Integer.toString(tabel.getCountCentralEmployee()));
									sumCentralEmployee += tabel.getCountCentralEmployee();
								}
								if (tabel.getCountBadEmployee() != 0) {
									badRatingEmployee.setText(Integer.toString(tabel.getCountBadEmployee()));
									sumBadEmployee += tabel.getCountBadEmployee();
								}
								

								if (tabel.getCountGoodAutist() != 0) {
									goodRatingAutist.setText(Integer.toString(tabel.getCountGoodAutist()));
									sumGoodAutist += tabel.getCountGoodAutist();
								}
								if (tabel.getCountCentralutist() != 0) {
									centralRatingAutist.setText(Integer.toString(tabel.getCountCentralutist()));
									sumCentralutist += tabel.getCountCentralutist();
								}
								if (tabel.getCountBadAutist() != 0) {
									badRatingAutist.setText(Integer.toString(tabel.getCountBadAutist()));
									sumBadAutist += tabel.getCountBadAutist();
								}
								
								statisticData.addView(categorie);
								statisticData.addView(border1);
								statisticData.addView(goodRatingEmployee);
								statisticData.addView(centralRatingEmployee);
								statisticData.addView(badRatingEmployee);
								statisticData.addView(border2);
								statisticData.addView(goodRatingAutist);
								statisticData.addView(centralRatingAutist);
								statisticData.addView(badRatingAutist);
										
								TableLayout t = (TableLayout)findViewById(R.id.lyo);
								t.addView(statisticData, tableRowParams);
								t.addView(v);
							}
						}
					}
					
					// Summen
					TableRow statisticSum = new TableRow(this);
					tableRowParams.setMargins(5, 0, 0, 0);
					statisticSum.setLayoutParams(tableRowParams);
							
					LayoutParams rowParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					rowParams.setMargins(5, 0, 0, 0);
					LayoutParams rowParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					LayoutParams rowCentral = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					rowCentral.setMargins(15, 0, 0, 0);
							
					TextView sum = new TextView(this);
					sum.setWidth(52);
					sum.setTextSize(11);
					sum.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					sum.setLayoutParams(rowParams2);

					ImageView border1 = new ImageView(this);
					LayoutParams imageParams =new LayoutParams(2, LayoutParams.FILL_PARENT);
					border1.setLayoutParams(imageParams);
					border1.setBackgroundColor(Color.parseColor("#FF909090"));
							
					TextView goodRatingEmployee = new TextView(this);
					goodRatingEmployee.setWidth(20);
					goodRatingEmployee.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					goodRatingEmployee.setLayoutParams(rowParams);
					goodRatingEmployee.setTextSize(11);
					goodRatingEmployee.setLayoutParams(rowParams);
							
					TextView centralRatingEmployee = new TextView(this);
					centralRatingEmployee.setWidth(65);
					centralRatingEmployee.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					centralRatingEmployee.setTextSize(11);
					centralRatingEmployee.setLayoutParams(rowCentral);
						
					TextView badRatingEmployee = new TextView(this);
					badRatingEmployee.setWidth(50);
					badRatingEmployee.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					badRatingEmployee.setTextSize(11);
					badRatingEmployee.setLayoutParams(rowParams);

					ImageView border2 = new ImageView(this);
					LayoutParams imageParams2 =new LayoutParams(2, LayoutParams.FILL_PARENT);
					border2.setLayoutParams(imageParams2);
					border2.setBackgroundColor(Color.parseColor("#FF909090"));
							
					TextView goodRatingAutist = new TextView(this);
					goodRatingAutist.setWidth(25);
					goodRatingAutist.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					goodRatingAutist.setTextSize(11);
					goodRatingAutist.setLayoutParams(rowParams2);

					TextView centralRatingAutist = new TextView(this);
					centralRatingAutist.setWidth(25);
					centralRatingAutist.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					centralRatingAutist.setTextSize(11);
					centralRatingAutist.setLayoutParams(rowParams);

					TextView badRatingAutist = new TextView(this);
					badRatingAutist.setWidth(35);
					badRatingAutist.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					badRatingAutist.setTextSize(11);
					badRatingAutist.setLayoutParams(rowParams);
							
					View v = new View(this);
					LayoutParams viewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 2);
					v.setLayoutParams(viewParams);
					v.setBackgroundColor(Color.parseColor("#FF909090"));

					sum.setText("Sum");
					goodRatingEmployee.setText(Integer.toString(sumGoodEmployee));
					centralRatingEmployee.setText(Integer.toString(sumCentralEmployee));
					badRatingEmployee.setText(Integer.toString(sumBadEmployee));
					
					goodRatingAutist.setText(Integer.toString(sumGoodAutist));
					centralRatingAutist.setText(Integer.toString(sumCentralutist));
					badRatingAutist.setText(Integer.toString(sumBadAutist));

					
					statisticSum.addView(sum);
					statisticSum.addView(border1);
					statisticSum.addView(goodRatingEmployee);
					statisticSum.addView(centralRatingEmployee);
					statisticSum.addView(badRatingEmployee);
					statisticSum.addView(border2);
					statisticSum.addView(goodRatingAutist);
					statisticSum.addView(centralRatingAutist);
					statisticSum.addView(badRatingAutist);
							
					TableLayout t = (TableLayout)findViewById(R.id.lyo);
					t.addView(statisticSum, tableRowParams);
					t.addView(v);

					// Prozent
					TableRow statisticPercent = new TableRow(this);
					tableRowParams.setMargins(5, 0, 0, 0);
					statisticPercent.setLayoutParams(tableRowParams);
							
					LayoutParams rowParamsPercent = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					rowParamsPercent.setMargins(5, 0, 0, 0);
					LayoutParams rowParams2Percent = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					LayoutParams rowCentralPercent = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					rowCentralPercent.setMargins(15, 0, 0, 0);
							
					TextView percent = new TextView(this);
					percent.setWidth(52);
					percent.setTextSize(11);
					percent.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					percent.setLayoutParams(rowParams2);

					ImageView border1Percent = new ImageView(this);
					LayoutParams imageParamsPercent =new LayoutParams(2, LayoutParams.FILL_PARENT);
					border1Percent.setLayoutParams(imageParams);
					border1Percent.setBackgroundColor(Color.parseColor("#FF909090"));
							// Color.parseColor("#FF909090")
							
					TextView goodRatingEmployeePercent = new TextView(this);
					goodRatingEmployeePercent.setWidth(20);
					goodRatingEmployeePercent.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					goodRatingEmployeePercent.setLayoutParams(rowParams);
					goodRatingEmployeePercent.setTextSize(11);
					goodRatingEmployeePercent.setLayoutParams(rowParams);
							
					TextView centralRatingEmployeePercent = new TextView(this);
					centralRatingEmployeePercent.setWidth(65);
					centralRatingEmployeePercent.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					centralRatingEmployeePercent.setTextSize(11);
					centralRatingEmployeePercent.setLayoutParams(rowCentral);
						
					TextView badRatingEmployeePercent = new TextView(this);
					badRatingEmployeePercent.setWidth(50);
					badRatingEmployeePercent.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					badRatingEmployeePercent.setTextSize(11);
					badRatingEmployeePercent.setLayoutParams(rowParams);

					ImageView border2Percent = new ImageView(this);
					LayoutParams imageParams2Percent =new LayoutParams(2, LayoutParams.FILL_PARENT);
					border2Percent.setLayoutParams(imageParams2Percent);
					border2Percent.setBackgroundColor(Color.parseColor("#FF909090"));
							
					TextView goodRatingAutistPercent = new TextView(this);
					goodRatingAutistPercent.setWidth(25);
					goodRatingAutistPercent.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					goodRatingAutistPercent.setTextSize(11);
					goodRatingAutistPercent.setLayoutParams(rowParams2);

					TextView centralRatingAutistPercent = new TextView(this);
					centralRatingAutistPercent.setWidth(35);
					centralRatingAutistPercent.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					centralRatingAutistPercent.setTextSize(11);
					centralRatingAutistPercent.setLayoutParams(rowParams);

					TextView badRatingAutistPercent = new TextView(this);
					badRatingAutistPercent.setWidth(50);
					badRatingAutistPercent.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					badRatingAutistPercent.setTextSize(11);
					badRatingAutistPercent.setLayoutParams(rowParams);
							
					View vPercent = new View(this);
					LayoutParams viewParamsPercent = new LayoutParams(LayoutParams.WRAP_CONTENT, 2);
					vPercent.setLayoutParams(viewParamsPercent);
					vPercent.setBackgroundColor(Color.parseColor("#FF909090"));

					percent.setText("%");
					
					if (sumGoodEmployee == 0) {
						percentGoodEmployee = 0;
					} else {
						percentGoodEmployee = (double)(sumGoodEmployee * 100) / (double)(sumGoodEmployee + sumCentralEmployee + sumBadEmployee);
					}
					
					if (sumCentralEmployee == 0) {
						percentCentralEmployee = 0;
					} else {
						percentCentralEmployee = (double)(sumCentralEmployee * 100) / (double)(sumGoodEmployee + sumCentralEmployee + sumBadEmployee);
					}
					
					if (sumBadEmployee == 0) {
						percentBadEmployee = 0;
					} else {
						percentBadEmployee = (double)(sumBadEmployee * 100) / (double)(sumGoodEmployee + sumCentralEmployee + sumBadEmployee);
					}

					goodRatingEmployeePercent.setText(precision.format(percentGoodEmployee));
					centralRatingEmployeePercent.setText(precision.format(percentCentralEmployee));
					badRatingEmployeePercent.setText(precision.format(percentBadEmployee));
					
					if (sumGoodAutist == 0) {
						percentGoodAutist = 0;
					} else {
						percentGoodAutist = (double)(sumGoodAutist * 100) / (double)(sumGoodAutist + sumCentralutist + sumBadAutist);
					}
					
					if (sumCentralutist == 0) {
						percentCentralutist = 0;
					} else {
						percentCentralutist = (double)(sumCentralutist * 100) / (double)(sumGoodAutist + sumCentralutist + sumBadAutist);
					}
					
					if (sumBadAutist == 0) {
						percentBadAutist = 0;
					} else {
						percentBadAutist = (double)(sumBadAutist * 100) / (double)(sumGoodAutist + sumCentralutist + sumBadAutist);

					}
					
					goodRatingAutistPercent.setText(precision.format(percentGoodAutist));
					centralRatingAutistPercent.setText(precision.format(percentCentralutist));
					badRatingAutistPercent.setText(precision.format(percentBadAutist));

					statisticPercent.addView(percent);
					statisticPercent.addView(border1Percent);
					statisticPercent.addView(goodRatingEmployeePercent);
					statisticPercent.addView(centralRatingEmployeePercent);
					statisticPercent.addView(badRatingEmployeePercent);
					statisticPercent.addView(border2Percent);
					statisticPercent.addView(goodRatingAutistPercent);
					statisticPercent.addView(centralRatingAutistPercent);
					statisticPercent.addView(badRatingAutistPercent);
							
					TableLayout tPercent = (TableLayout)findViewById(R.id.lyo);
					tPercent.addView(statisticPercent, tableRowParams);
					tPercent.addView(vPercent);
			
			}
		}

		public void onClickCancel(View v) throws Exception {
		   //display file saved message
		   Intent i = new Intent(this, JobCoach.class);
		   startActivity(i);
		}	
}
	