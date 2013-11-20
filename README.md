xlsio
=====

A micro io utility to populate an excelsheet from template using JSON data.

Usage:(using curl for demonstration)

$ curl  --form template=@123.xls  http://localhost:9000/uploadTemplate
    EmOXxwGSRF
$ curl  --form json=@123.json  http://localhost:9000/uploadJson/EmOXxwGSRF
    uDBpf6VpxL
$ curl    http://localhost:9000/downloadFile/uDBpf6VpxL > file.xls



