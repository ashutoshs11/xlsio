#xlsio

A micro io utility to populate an excel-sheet from template using JSON data.
___
##Usage:
####(using curl for demonstration)

```
*$* curl  --form template=@123.xls  http://localhost:9000/uploadTemplate
EmOXxwGSRF
```
| Field1        | Field2           | Field3  |
|:-------------:|:----------------:|:-------:|
|               |                  |         |
|               |                  |         |
|               |                  |         |


```
$ curl  --form json=@123.json  http://localhost:9000/uploadJson/EmOXxwGSRF
uDBpf6VpxL

[ {
  "Field1" : 1",
  "Field2" : "data2_1",
  "Field3" : "data3_1"
}, {
  "Field1" : 2,
  "Field2" : "data2_2"
}, {
}, {
  "Field1" : 3,
  "Field2" : "data2_3",
  "Field3" : "data3_3"
}  ]


$ curl    http://localhost:9000/downloadFile/uDBpf6VpxL > file.xls
```

| Field1        | Field2           | Field3  |
| ------------- |:----------------:| -------:|
|  1            |   data2_1        |data3_1  |
|  2            |   data2_2        |         |
|  3            |   data2_3        |data3_3  |

___
