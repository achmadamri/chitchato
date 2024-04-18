import { Component, OnInit } from '@angular/core';
import Chart from 'chart.js';

// core components
import {
  chartOptions,
  parseOptions,
  chartExample1,
  chartExample2
} from "../../variables/charts";
import { UserService } from 'src/app/services/userservice';
import { GetUserResponse } from 'src/app/services/getuserresponse';
import { GetPersonaListResponse } from 'src/app/services/getpersonalistresponse';
import { Util } from 'src/app/util';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  util: Util = new Util();
  public datasets: any;
  public data: any;
  public salesChart;
  public clicked: boolean = true;
  public clicked1: boolean = false;
  public getUserResponse: GetUserResponse;
  public getPersonaListResponse: GetPersonaListResponse;

  constructor(
    private userService: UserService   
  ) { }

  ngOnInit() {

    this.userService.getUser().subscribe(data => {
      this.getUserResponse = data;
      
    }, error => {
      if (error.status === 403) {
        window.location.href = '/';        
      } else {        
        this.clicked = false;
        this.util.showNotification(4, 'bottom', 'center', 'Error getting user');
      }
    });

    this.datasets = [
      [0, 20, 10, 30, 15, 40, 20, 60, 60],
      [0, 20, 5, 25, 10, 30, 15, 40, 40]
    ];
    this.data = this.datasets[0];


    var chartOrders = document.getElementById('chart-orders');

    parseOptions(Chart, chartOptions());


    var ordersChart = new Chart(chartOrders, {
      type: 'bar',
      options: chartExample2.options,
      data: chartExample2.data
    });

    var chartSales = document.getElementById('chart-sales');

    this.salesChart = new Chart(chartSales, {
			type: 'line',
			options: chartExample1.options,
			data: chartExample1.data
		});
  }


  public updateOptions() {
    this.salesChart.data.datasets[0].data = this.data;
    this.salesChart.update();
  }

}
