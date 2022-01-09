<template>
  <a-layout>
    <a-layout>
      <a-layout-content
        :style="{
          background: '#fff',
          padding: '24px',
          margin: 0,
          minHeight: '280px',
        }"
      >
        <a-form layout="inline" :model="params">
          <a-form-item>
            <a-input placeholder="名称"> </a-input>
          </a-form-item>
          <a-form-item>
            <a-space size="small">
              <a-button type="primary" @click="handleQuery()"> 搜索 </a-button>
              <a-button type="primary" @click="add()"> 新增 </a-button>
            </a-space>
          </a-form-item>
        </a-form>
        <a-table
          :columns="columns"
          :row-key="(record) => record.id"
          :data-source="level1"
          :loading="loading"
          :pagination="false"
        >
          <template #cover="{ text: cover }">
            <img
              :src="cover.cover"
              alt="avater"
              style="width: 40px"
              v-if="cover"
            />
          </template>
          <template v-slot:action="{ text, record }">
            <a-space size="small">
              <a-button type="primary" @click="edit(record)"> 编辑 </a-button>
              <a-popconfirm
                title="删除后无法恢复，是否删除?"
                ok-text="是"
                cancel-text="否"
                @confirm="showDeleteConfirm(record)"
              >
                <a-button type="danger"> 删除 </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </a-table>
      </a-layout-content>
    </a-layout>
  </a-layout>
  <a-modal
    title="文档表单"
    v-model:visible="modelVisible"
    :confirm-loading="modelLoading"
    @ok="handleModelOk"
  >
    <a-form :model="doc" :label-col="{ span: 6 }">
      <a-form-item label="名称">
        <a-input v-model:value="doc.name" />
      </a-form-item>
      <a-form-item label="父文档">
        <a-tree-select
          v-model:value="doc.parent"
          style="width: 100%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="level1"
          placeholder="请选择父文档"
          tree-default-expand-all
          :replaceFields="{
            title: 'name',
            key: 'id',
            value: 'id',
          }"
        >
        </a-tree-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="doc.sort" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, createVNode } from "vue";
import axios from "axios";
import { message, Modal } from "ant-design-vue";
import { Tool } from "@/util/tool";
import { useRoute } from "vue-router";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";

export default defineComponent({
  name: "AdminDoc",
  setup() {
    // 删除确认对话框
    const route = useRoute();
    const docs = ref();
    const columns = [
      {
        title: "名称",
        dataIndex: "name",
      },
      {
        title: "父文档",
        dataIndex: "parent",
        key: "parent",
      },
      {
        title: "顺序",
        key: "sort",
        dataIndex: "sort",
      },
      {
        title: "Action",
        key: "action",
        slots: { customRender: "action" },
      },
    ];
    /**
     * 一级文档树，children属性就是二级文档
     * [{
     *  id: "",
     *  name: "",
     *  children: [{
     *    id: "",
     *    name: "",
     *  }]
     * }]
     */

    // 一级文档树,children属性就是二级文档
    const level1 = ref();
    // 数据查询
    const handleQuery = () => {
      axios.get("/doc/all").then((response) => {
        const data = response.data;
        if (data.success) {
          docs.value = data.content;
          console.log("原始数据:", doc.value);
          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
          console.log("树形结构:", level1);
          // 重置分页按钮
        } else {
          message.error(data.message);
        }
      });
    };

    // 表单
    const doc = ref({});
    const modelVisible = ref(false);
    const modelLoading = ref(false);

    const handleModelOk = () => {
      modelLoading.value = true;
      axios.post("/doc/save", doc.value).then((response) => {
        const data = response.data;
        modelVisible.value = false;
        // data = CommonResp
        if (data.success) {
          modelLoading.value = false;
          // 重新加载列表
          handleQuery();
        } else {
          message.error(data.message);
          modelLoading.value = false;
        }
      });
    };
    //拷贝level1 用来在前端修改增加一个"无"
    const treeSelectData = ref();
    treeSelectData.value = [];

    /**
     * 将某节点及子孙节点全部设置为disable
     */
    const setDisable = (treeSelectData: any, id: any) => {
      // 遍历数组,即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          //如果当前节点就是目标节点
          console.log("disable", node);
          //将目标节点设置为disable
          node.disabled = true;

          //遍历所有子节点,将所有子节点全部加上disable
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              setDisable(children, children[j].id);
            }
          }
        }
      }
    };
    /**
     * 编辑
     */
    const edit = (record: any) => {
      modelVisible.value = true;
      doc.value = Tool.copy(record);

      // 不能选择当前节点及其所以有子孙节点,作为父节点,会使树断开
      treeSelectData.value = Tool.copy(level1.value);
      setDisable(treeSelectData.value, record.id);

      // 为选择树添加一个"无"
      treeSelectData.value.unshift({ id: 0, name: "无" });
    };

    /**
     * 新增
     */
    const add = () => {
      modelVisible.value = true;
      doc.value = {
        ebookId: route.query.ebookId,
      };

      treeSelectData.value = Tool.copy(level1.value);
      // 为选择树添加一个"无"
      treeSelectData.value.unshift({ id: 0, name: "无" });
    };

    const ids: Array<string> = [];

    /**
     * 查找整根树枝获取id
     */
    const getDeleteIDs = (treeSelectData: any, id: any) => {
      // 遍历数组,即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          //如果当前节点就是目标节点
          console.log("id", id);
          //将目标id放入结果集ids
          // node.disabled = true;
          ids.push(id);

          //遍历所有子节点
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getDeleteIDs(children, id);
            }
          }
        }
      }
    };

    const deleteNames: Array<string> = [];
    /**
     * 查找整根树枝获取name
     */
    const getDeleteNames = (treeSelectData: any, id: any) => {
      console.log("getDeleteNamesID", id);
      // init
      deleteNames.length = 0;
      // 遍历数组,即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          //如果当前节点就是目标节点
          console.log("deleteName", node);
          //将目标name放入结果集names
          // node.disabled = true;
          deleteNames.push(node.name);

          //遍历所有子节点
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getDeleteNames(children, id);
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            getDeleteNames(children, id);
          }
        }
      }
    };

    /**
     * 删除
     */
    const handleDelete = (id: number) => {
      getDeleteIDs(level1.value, id);
      axios.delete("/doc/delete/" + ids.join(",")).then((response) => {
        const data = response.data;
        // data = CommonResp
        if (data.success) {
          // 重新加载列表
          handleQuery();
        }
      });
    };

    // 确认删除对话框
    const showDeleteConfirm = (record: any) => {
      console.log(record.id);
      getDeleteNames(level1.value, record.id);
      Modal.confirm({
        title: () => "重要提示",
        icon: () => createVNode(ExclamationCircleOutlined),
        content: () =>
          "将删除：【" + deleteNames + "】删除后不可恢复，确认删除？",
        okText: () => "确认",
        okType: "danger",
        cancelText: () => "取消",
        onOk() {
          handleDelete(record.id);
        },
        // onCancel() {
        //   console.log("Cancel");
        // },
      });
    };

    onMounted(() => {
      handleQuery();
    });
    return {
      // 表格类
      docs,
      columns,
      level1,
      treeSelectData,

      // 功能
      edit,
      add,
      handleDelete,

      // 表单类
      modelVisible,
      modelLoading,
      handleModelOk,
      doc,

      // 查询
      handleQuery,

      showDeleteConfirm,
    };
  },
});
</script>
