# WCS层调用接口
## 分配库位
1. 路径：GET `http://localhost:8080/task/getLocation`
2. 传入参数：无
3. Return：
 - code：200正常, 500异常
 - data：
```
   "allowInbound": true,
   "allowOutbound": true,
   "cargoStatus": "EMPTY",
   "code": "A-001-001-003-01",
   "column": 1,
   "concurrencyStamp": "",
   "depth": 1,
   "depthNum": 1,
   "direction": "WEST_UP",
   "extraProperties": "",
   "height": 0.00,
   "id": "3a17720d-9cc2-5857-e208-fcfb7888e687",
   "inboundMode": "AUTOMATIC",
   "inboundTrigMode": "NONE",
   "isAllowReceipt": false,
   "isAllowSelect": false,
   "isDeleted": false,
   "isStocktake": false,
   "isVirtually": false,
   "isWithPallet": true,
   "layer": 3,
   "locationAreaId": "3a17720d-9c05-608c-26b1-c7c096d7f815",
   "lockContainerBarcode": "",
   "lockState": "NONE",
   "maxEmptyPalletNum": 1,
   "maxFullPalletNum": 1,
   "receiptMode": "NRWP",
   "row": 1,
   "shortCode": "A-001-001-003-01",
   "warehouseId": "3a17720d-9b95-f2b2-698a-4bf204c10f8f",
   "width": 0.00,
   "x": 5.00,
   "y": 12.00
```
 - message：处理提示信息
 - success：是否成功, 例如:true
 - timestamp：处理时间, 例如:2026-05-12T08:59:03.8627698


## 完成入库
1. 路径：POST `http://localhost:8080/task/finishedInbound`
2. 传入参数:locationAreaId, rowNo, columnNo, containerId, locationId
3. Return:
 - code：200正常, 500异常
 - data: 无
 - message：处理提示信息
 - success：是否成功, 例如:true
 - timestamp：处理时间, 例如:2026-05-12T08:59:03.8627698

## 获取出库任务
1. 路径：GET `http://localhost:8080/task/getTask`
2. 传入参数:无
3. Return:
 - code：200正常, 500异常
 - data:
```
   "containerId": "PT0080",
   "createTime": "2026-05-11T11:04:24",
   "executeTime": null,
   "finishTime": null,
   "materialId": "MAT001",
   "sourceLocationId": "3a17720d-9c05-608c-26b1-c7c096d7f815",
   "status": "CREATED",
   "targetLocationId": null,
   "taskId": "97c20162-643f-4397-ae91-9f3c0cacd4a0",
   "taskType": "OUT"
```
 - message：处理提示信息
 - success：是否成功, 例如:true
 - timestamp：处理时间, 例如:2026-05-12T08:59:03.8627698

## 完成出库
1. 路径：POST `http://localhost:8080/task/finishedOutbound?id={taskId}&status={status}`
2. 传入参数: 任务ID`{taskId}`,状态`{status}`
3. Return: 
 - code：200正常, 500异常
 - data: 无
 - message：处理提示信息
 - success：是否成功, 例如:true
 - timestamp：处理时间, 例如:2026-05-12T08:59:03.8627698

