(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-3a6bcca8"],{"6a5c":function(e,t,a){"use strict";a.r(t);var n=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"app-container"},[a("el-row",{attrs:{gutter:20,justify:"center",type:"flex"}},[a("el-col",{attrs:{span:8,justify:"center"}},[a("el-tag",[e._v("模板图片")]),e._v(" "),a("div",{staticClass:"grid-content bg-purple",staticStyle:{width:"300px",height:"350px"}},[a("el-image",{ref:"lazyImg",staticClass:"vx-lazyLoad",staticStyle:{width:"300px"},attrs:{lazy:"",src:e.monitor.modelFace,fit:e.fit,"preview-src-list":[e.monitor.modelFace]}},[a("div",{staticClass:"image-slot",attrs:{slot:"placeholder"},slot:"placeholder"},[a("i",{staticClass:"el-icon-loading"}),e._v("加载中\n          ")]),e._v(" "),a("div",{staticClass:"image-slot",attrs:{slot:"error"},slot:"error"},[a("i",{staticClass:"el-icon-picture-outline"})])])],1),e._v(" "),a("el-tag",[e._v("抓拍图片")]),e._v(" "),a("div",{staticClass:"grid-content bg-purple"},[a("el-image",{ref:"lazyImg",staticClass:"vx-lazyLoad",staticStyle:{width:"300px"},attrs:{lazy:"",src:e.monitor.face,fit:e.fit,"preview-src-list":[e.monitor.face]}},[a("div",{staticClass:"image-slot",attrs:{slot:"placeholder"},slot:"placeholder"},[a("i",{staticClass:"el-icon-loading"}),e._v("加载中\n          ")]),e._v(" "),a("div",{staticClass:"image-slot",attrs:{slot:"error"},slot:"error"},[a("i",{staticClass:"el-icon-picture-outline"})])])],1)],1),e._v(" "),a("el-col",{attrs:{span:8}},[a("div",{staticClass:"grid-content bg-purple"},[a("el-form",{ref:"form",attrs:{model:e.form,"label-width":"80px"}},[a("el-form-item",{attrs:{label:"姓名"}},[a("el-input",{attrs:{disabled:"true"},model:{value:e.monitor.userName,callback:function(t){e.$set(e.monitor,"userName",t)},expression:"monitor.userName"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"开门方式"}},[a("el-input",{attrs:{disabled:"true"},model:{value:e.monitor.openTypeCdName,callback:function(t){e.$set(e.monitor,"openTypeCdName",t)},expression:"monitor.openTypeCdName"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"是否带帽"}},[a("el-input",{attrs:{disabled:"true"},model:{value:e.monitor.hatName,callback:function(t){e.$set(e.monitor,"hatName",t)},expression:"monitor.hatName"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"相似度"}},[e.monitor.similarity>0?a("el-tag",{attrs:{type:"success"}},[e._v(e._s(e.monitor.similarity))]):a("el-tag",{attrs:{type:"danger"}},[e._v("开门失败")])],1),e._v(" "),a("el-form-item",{attrs:{label:"欠费情况"}},[a("el-input",{attrs:{disabled:"true"},model:{value:e.monitor.amountOwed,callback:function(t){e.$set(e.monitor,"amountOwed",t)},expression:"monitor.amountOwed"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"门禁名称"}},[a("el-input",{attrs:{disabled:"true"},model:{value:e.monitor.machineName,callback:function(t){e.$set(e.monitor,"machineName",t)},expression:"monitor.machineName"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"门禁编码"}},[a("el-input",{attrs:{disabled:"true"},model:{value:e.monitor.machineCode,callback:function(t){e.$set(e.monitor,"machineCode",t)},expression:"monitor.machineCode"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"门禁IP"}},[a("el-input",{attrs:{disabled:"true"},model:{value:e.monitor.machineIp,callback:function(t){e.$set(e.monitor,"machineIp",t)},expression:"monitor.machineIp"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"开门时间"}},[a("el-input",{attrs:{disabled:"true"},model:{value:e.monitor.createTime,callback:function(t){e.$set(e.monitor,"createTime",t)},expression:"monitor.createTime"}})],1)],1)],1)])],1)],1)},i=[],o=(a("9c3e"),a("ed08"),{filters:{statusFilter:function(e){var t={published:"success",draft:"gray",deleted:"danger"};return t[e]}},data:function(){return{monitor:{modelFace:"",face:"",userName:"",openTypeCdName:"",hatName:"",similarity:"",amountOwed:"",machineName:"",machineId:"",machineCode:"",machineIp:"",createTime:""},listLoading:!0}},created:function(){this.monitor.machineId=this.$route.query.machineId,this.fetchData()},methods:{fetchData:function(){var e=null,t=this,a="ws://"+window.location.host+"/openDoorMonitor/"+this.uuid()+"/"+this.monitor.machineId;e="WebSocket"in window?new WebSocket(a):"MozWebSocket"in window?new MozWebSocket(a):new SockJS(a),e.onerror=function(){console.log("初始化失败"),this.$notify.error({title:"错误",message:"连接失败，请检查网络"})},e.onopen=function(){console.log("ws初始化成功")},e.onmessage=function(e){console.log("event",e),t.freshMonitor(e.data)},e.onclose=function(){console.log("初始化失败"),this.$notify.error({title:"错误",message:"连接关闭，请刷新浏览器"})},window.onbeforeunload=function(){e.close()}},freshMonitor:function(e){console.log("_data",e),e=JSON.parse(e),this.monitor={modelFace:e.modelFace,face:e.face,userName:e.userName,openTypeCdName:"1000"==e.openTypeCd?"人脸":"其他",hatName:"1"==e.hat?"无":"有",similarity:e.similarity,amountOwed:"0",machineName:e.machineName,machineId:e.machineId,machineCode:e.machineCode,machineIp:e.machineIp,createTime:e.createTime}},uuid:function(){for(var e=[],t="0123456789abcdef",a=0;a<36;a++)e[a]=t.substr(Math.floor(16*Math.random()),1);e[14]="4",e[19]=t.substr(3&e[19]|8,1),e[8]=e[13]=e[18]=e[23]="-";var n=e.join("");return n}}}),r=o,c=a("2877"),s=Object(c["a"])(r,n,i,!1,null,null,null);t["default"]=s.exports},"9c3e":function(e,t,a){"use strict";a.d(t,"g",(function(){return i})),a.d(t,"h",(function(){return o})),a.d(t,"b",(function(){return r})),a.d(t,"o",(function(){return c})),a.d(t,"d",(function(){return s})),a.d(t,"i",(function(){return l})),a.d(t,"e",(function(){return m})),a.d(t,"a",(function(){return u})),a.d(t,"n",(function(){return d})),a.d(t,"m",(function(){return h})),a.d(t,"l",(function(){return p})),a.d(t,"f",(function(){return f})),a.d(t,"c",(function(){return b})),a.d(t,"j",(function(){return g})),a.d(t,"k",(function(){return v}));var n=a("b775");function i(e){return Object(n["a"])({url:"/api/machine/getMachineCmds",method:"get",params:{page:1,row:10,machineTypeCd:"9998"}})}function o(e){return Object(n["a"])({url:"/api/machine/getMachineCmds",method:"get",params:e})}function r(e){return Object(n["a"])({url:"/api/machine/deleteMachineCmd",method:"post",data:e})}function c(e){return Object(n["a"])({url:"/api/machine/saveMachineCmd",method:"post",data:e})}function s(e){return Object(n["a"])({url:"/api/machine/getMachines",method:"get",params:{page:1,row:10,machineTypeCd:"9998"}})}function l(e){return Object(n["a"])({url:"/api/machine/getMachineCodes",method:"get",params:e})}function m(e){return Object(n["a"])({url:"/api/machine/getMachines",method:"get",params:e})}function u(e){return Object(n["a"])({url:"/api/machine/deleteMachine",method:"post",data:e})}function d(e){return Object(n["a"])({url:"/api/machine/saveMachine",method:"post",data:e})}function h(e){return Object(n["a"])({url:"/api/machine/startMachine",method:"post",data:e})}function p(e){return Object(n["a"])({url:"/api/machine/openDoor",method:"post",data:e})}function f(e){return Object(n["a"])({url:"/api/machine/getMachineLogs",method:"get",params:e})}function b(e){return Object(n["a"])({url:"/api/machine/getMachineFaces",method:"get",params:e})}function g(e){return Object(n["a"])({url:"/api/machine/getMachineOpenDoors",method:"get",params:e})}function v(e){return Object(n["a"])({url:"/api/machine/getTranLogs",method:"get",params:e})}}}]);