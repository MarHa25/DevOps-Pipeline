/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9958333333333333, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.875, 500, 1500, "Organiser Login POST"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Delete Organisation POST-1"], "isController": false}, {"data": [1.0, 500, 1500, "Admin View Organisation Info GET"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Delete Organisation POST-0"], "isController": false}, {"data": [1.0, 500, 1500, "Organiser Logout"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Edit Organisation Info POST"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Edit Block Info GET"], "isController": false}, {"data": [1.0, 500, 1500, "Organiser Edit Organisation Info POST"], "isController": false}, {"data": [1.0, 500, 1500, "Organiser View Organisations GET"], "isController": false}, {"data": [1.0, 500, 1500, "Organiser Edit Organisation Info GET"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Map / Main Street"], "isController": false}, {"data": [1.0, 500, 1500, "Admin View All Organistations After Delete GET"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Login POST"], "isController": false}, {"data": [1.0, 500, 1500, "Organiser Login POST-0"], "isController": false}, {"data": [1.0, 500, 1500, "Organiser Login POST-1"], "isController": false}, {"data": [1.0, 500, 1500, "Organiser View Organisation Info GET"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Edit Organisation Info POST-1"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Edit Organisation Info GET"], "isController": false}, {"data": [1.0, 500, 1500, "Organiser Edit Organisation Info POST-1"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Login POST-1"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Edit Organisation Info POST-0"], "isController": false}, {"data": [1.0, 500, 1500, "Organiser Map / Main Street"], "isController": false}, {"data": [1.0, 500, 1500, "Organiser Edit Organisation Info POST-0"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Login POST-0"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Logout"], "isController": false}, {"data": [1.0, 500, 1500, "Admin View All Organistations GET"], "isController": false}, {"data": [1.0, 500, 1500, "Organiser Login GET"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Delete Organisation POST"], "isController": false}, {"data": [1.0, 500, 1500, "Admin Login GET"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 120, 0, 0.0, 75.38333333333331, 25, 616, 48.0, 104.0, 185.84999999999997, 589.959999999999, 34.51251078515962, 556.8689670513373, 10.053790983606557], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["Organiser Login POST", 4, 0, 0.0, 313.0, 89, 616, 273.5, 616.0, 616.0, 616.0, 1.9221528111484865, 70.75925036040366, 0.9441824843825084], "isController": false}, {"data": ["Admin Delete Organisation POST-1", 4, 0, 0.0, 59.75, 51, 68, 60.0, 68.0, 68.0, 68.0, 3.4965034965034967, 126.11314466783217, 0.6282779720279721], "isController": false}, {"data": ["Admin View Organisation Info GET", 4, 0, 0.0, 42.5, 39, 46, 42.5, 46.0, 46.0, 46.0, 3.433476394849785, 27.389015557939913, 0.6437768240343348], "isController": false}, {"data": ["Admin Delete Organisation POST-0", 4, 0, 0.0, 31.0, 25, 39, 30.0, 39.0, 39.0, 39.0, 3.5398230088495577, 0.6706305309734514, 0.9852046460176992], "isController": false}, {"data": ["Organiser Logout", 4, 0, 0.0, 40.0, 33, 48, 39.5, 48.0, 48.0, 48.0, 3.2388663967611335, 19.188069331983804, 0.566169028340081], "isController": false}, {"data": ["Admin Edit Organisation Info POST", 4, 0, 0.0, 79.75, 77, 83, 79.5, 83.0, 83.0, 83.0, 3.369839932603201, 27.083772114574558, 2.6392691659646164], "isController": false}, {"data": ["Admin Edit Block Info GET", 4, 0, 0.0, 38.25, 32, 44, 38.5, 44.0, 44.0, 44.0, 3.4662045060658575, 30.002640272963607, 0.6499133448873484], "isController": false}, {"data": ["Organiser Edit Organisation Info POST", 4, 0, 0.0, 87.0, 59, 115, 87.0, 115.0, 115.0, 115.0, 3.0165912518853695, 24.53484398567119, 2.4009002639517343], "isController": false}, {"data": ["Organiser View Organisations GET", 4, 0, 0.0, 58.0, 30, 88, 57.0, 88.0, 88.0, 88.0, 2.7548209366391188, 30.588197314049587, 0.5272899449035813], "isController": false}, {"data": ["Organiser Edit Organisation Info GET", 8, 0, 0.0, 46.625, 34, 72, 40.5, 72.0, 72.0, 72.0, 5.578800557880055, 48.00206480997211, 1.0460251046025104], "isController": false}, {"data": ["Admin Map / Main Street", 4, 0, 0.0, 69.0, 65, 73, 69.0, 73.0, 73.0, 73.0, 3.236245954692557, 118.33788430420712, 0.5593901699029127], "isController": false}, {"data": ["Admin View All Organistations After Delete GET", 4, 0, 0.0, 65.0, 40, 87, 66.5, 87.0, 87.0, 87.0, 3.561887800534283, 128.47144924309885, 0.640026714158504], "isController": false}, {"data": ["Admin Login POST", 4, 0, 0.0, 101.0, 98, 104, 101.0, 104.0, 104.0, 104.0, 3.147128245476003, 115.6538896538159, 1.5735641227380017], "isController": false}, {"data": ["Organiser Login POST-0", 4, 0, 0.0, 68.0, 25, 186, 30.5, 186.0, 186.0, 186.0, 1.981178801386825, 0.5920319465081724, 0.5455980683506686], "isController": false}, {"data": ["Organiser Login POST-1", 4, 0, 0.0, 243.0, 62, 427, 241.5, 427.0, 427.0, 427.0, 2.1141649048625792, 77.19592362579282, 0.45627973044397463], "isController": false}, {"data": ["Organiser View Organisation Info GET", 4, 0, 0.0, 66.75, 39, 93, 67.5, 93.0, 93.0, 93.0, 2.857142857142857, 22.487444196428573, 0.5357142857142857], "isController": false}, {"data": ["Admin Edit Organisation Info POST-1", 4, 0, 0.0, 35.25, 31, 38, 36.0, 38.0, 38.0, 38.0, 3.5087719298245617, 27.508223684210527, 0.6578947368421053], "isController": false}, {"data": ["Admin Edit Organisation Info GET", 4, 0, 0.0, 32.75, 29, 35, 33.5, 35.0, 35.0, 35.0, 3.5118525021949076, 27.527230575065847, 0.6584723441615452], "isController": false}, {"data": ["Organiser Edit Organisation Info POST-1", 4, 0, 0.0, 37.0, 28, 49, 35.5, 49.0, 49.0, 49.0, 3.1695721077654517, 25.153835677496037, 0.5942947702060222], "isController": false}, {"data": ["Admin Login POST-1", 4, 0, 0.0, 68.75, 65, 72, 69.0, 72.0, 72.0, 72.0, 3.228410008071025, 118.05135189669087, 0.5580357142857142], "isController": false}, {"data": ["Admin Edit Organisation Info POST-0", 4, 0, 0.0, 44.25, 39, 48, 45.0, 48.0, 48.0, 48.0, 3.475238922675934, 0.6855451781059948, 2.070210686359687], "isController": false}, {"data": ["Organiser Map / Main Street", 4, 0, 0.0, 84.5, 58, 117, 81.5, 117.0, 117.0, 117.0, 2.609262883235486, 95.27376875407698, 0.4510151663405088], "isController": false}, {"data": ["Organiser Edit Organisation Info POST-0", 4, 0, 0.0, 49.5, 30, 72, 48.0, 72.0, 72.0, 72.0, 3.0840400925212026, 0.608375096376253, 1.8763251734772552], "isController": false}, {"data": ["Admin Login POST-0", 4, 0, 0.0, 31.5, 30, 32, 32.0, 32.0, 32.0, 32.0, 3.3195020746887964, 0.6061981327800829, 1.0859699170124482], "isController": false}, {"data": ["Admin Logout", 4, 0, 0.0, 28.5, 25, 32, 28.5, 32.0, 32.0, 32.0, 3.7735849056603774, 22.35038325471698, 0.6596403301886792], "isController": false}, {"data": ["Admin View All Organistations GET", 4, 0, 0.0, 74.5, 55, 95, 74.0, 95.0, 95.0, 95.0, 3.273322422258593, 119.07509461947627, 0.5881751227495908], "isController": false}, {"data": ["Organiser Login GET", 4, 0, 0.0, 187.0, 36, 492, 110.0, 492.0, 492.0, 492.0, 1.6090104585679805, 11.296070494770715, 0.21212540225261461], "isController": false}, {"data": ["Admin Delete Organisation POST", 4, 0, 0.0, 91.5, 77, 107, 91.0, 107.0, 107.0, 107.0, 3.3812341504649197, 122.59615384615384, 1.5486316568047336], "isController": false}, {"data": ["Admin Login GET", 4, 0, 0.0, 41.25, 33, 51, 40.5, 51.0, 51.0, 51.0, 3.2679738562091503, 22.977941176470587, 0.5872140522875817], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 120, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
