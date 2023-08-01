import {
    getParkingBoxs,
    deleteParkingBoxs,
    saveParkingBoxs,
  } from "@/api/parkingBox";
  import {
    getParkingAreas
  } from '@/api/parkingArea';
  import Pagination from "@/components/Pagination";
  
  export default {
    filters: {
      statusFilter(status) {
        const statusMap = {
          published: "success",
          draft: "gray",
          deleted: "danger",
        };
        return statusMap[status];
      },
    },
    components: { Pagination },
    data() {
      return {
        listQuery: {
          page: 1,
          row: 10,
          boxId: "",
          boxName: ""
        },
        list: null,
        listLoading: true,
        protocols: null, //设备厂商
        parkingAreas: [], //停车场
        deleteParkingBoxDailogVisible: false,
        dialogFormVisible: false,
        curParkingBox: {},
        total: 0,
        temp: {
          boxName: "",
          tempCarIn: "",
          fee: "",
          blueCarIn: "",
          yelowCarIn: "",
          extBoxId: "",
          paId:''
        },
        rules: {},
      };
    },
    created() {
      this.fetchData();
    },
    methods: {
      fetchData() {
        this.listLoading = true;
        getParkingBoxs(this.listQuery).then((response) => {
          this.list = response.data;
          this.total = response.total;
          this.listLoading = false;
        });
  
        getParkingAreas({
          a:'hc'
        }).then(_data=>{
          this.parkingAreas = _data.data;
        })
      },
      addParkingBox() {
        this.dialogFormVisible = true;
        this.queryProtocol();
        this.queryParkingAreas();
      },
      deleteParkingBox(_row) {
        this.deleteParkingBoxDailogVisible = true;
        this.curParkingBox = _row;
      },
      doDeleteParkingBox() {
        this.listLoading = true;
        deleteParkingBoxs(this.curParkingBox).then((response) => {
          this.listLoading = false;
          this.$message({
            type: "info",
            message: response.msg,
          });
          this.deleteParkingBoxDailogVisible = false;
          this.fetchData();
        });
      },
      saveParkingBoxInfo() {
        this.listLoading = true;
        saveParkingBoxs(this.temp).then((response) => {
          this.listLoading = false;
          this.dialogFormVisible = false;
          this.fetchData();
        });
      },
    },
  };