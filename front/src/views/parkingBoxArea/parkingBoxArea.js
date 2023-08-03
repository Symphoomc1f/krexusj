import {
  getParkingBoxAreas,
  deleteParkingBoxAreas,
  saveParkingBoxAreas,
  updateParkingBoxArea
} from "@/api/parkingBoxArea";
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
      deleteParkingBoxAreaDailogVisible: false,
      dialogFormVisible: false,
      curParkingBoxArea: {},
      total: 0,
      temp: {
        paId: "",
        boxId: "",
        defaultArea: "",
      },
      rules: {},
    };
  },
  created() {
    this.listQuery.boxId = this.$route.query.boxId; //接收参数
    this.temp.boxId = this.listQuery.boxId;
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      getParkingBoxAreas(this.listQuery).then((response) => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });

      getParkingAreas({
        a: 'hc'
      }).then(_data => {
        this.parkingAreas = _data.data;
      })
    },
    addParkingBoxArea() {
      this.dialogFormVisible = true;
    },
    editParkingBoxArea: function (_row) {
      this.dialogFormVisible = true;
      this.temp = _row;
    },
    deleteParkingBoxArea(_row) {
      this.deleteParkingBoxAreaDailogVisible = true;
      this.curParkingBoxArea = _row;
    },
    doDeleteParkingBoxArea() {
      this.listLoading = true;
      deleteParkingBoxAreas(this.curParkingBoxArea).then((response) => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg,
        });
        this.deleteParkingBoxAreaDailogVisible = false;
        this.fetchData();
      });
    },
    saveParkingBoxAreaInfo() {
      this.listLoading = true;
      saveParkingBoxAreas(this.temp).then((response) => {
        this.listLoading = false;
        this.dialogFormVisible = false;
        this.fetchData();
      });
    },
    _defaultArea: function (_row) {
      this.listLoading = true;
      updateParkingBoxArea({
        baId: _row.baId,
        defaultArea: 'T'
      }).then((response) => {
        this.listLoading = false;
        this.fetchData();
      });
    }
  },
};