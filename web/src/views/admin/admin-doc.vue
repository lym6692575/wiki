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
        <a-row :gutter="24">
          <a-col :span="8">
            <a-form layout="inline" :model="params">
              <a-form-item>
                <a-input placeholder="名称"> </a-input>
              </a-form-item>
              <a-form-item>
                <a-space size="small">
                  <a-button type="primary" @click="handleQuery()">
                    搜索
                  </a-button>
                  <a-button type="primary" @click="add()"> 新增 </a-button>
                </a-space>
              </a-form-item>
            </a-form>
            <a-table
              v-if="level1 && level1.length > 0"
              :columns="columns"
              :row-key="(record) => record.id"
              :data-source="level1"
              :loading="loading"
              :pagination="false"
              size="small"
              :defaultExpandAllRows="true"
            >
              <template #name="{ text, record }">
                {{ record.sort }} {{ text }}
              </template>
              <template v-slot:action="{ text, record }">
                <a-space size="small">
                  <a-button type="primary" @click="edit(record)" size="small">
                    编辑
                  </a-button>
                  <a-popconfirm
                    title="删除后无法恢复，是否删除?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="showDeleteConfirm(record)"
                  >
                    <a-button type="danger" size="small"> 删除 </a-button>
                  </a-popconfirm>
                </a-space>
              </template>
            </a-table></a-col
          >
          <a-col :span="16">
            <p>
              <a-form layout="inline" :model="param">
                <a-form-item>
                  <a-button type="primary" @click="handleSave">保存</a-button>
                </a-form-item>
              </a-form>
            </p>
            <a-form :model="doc" :layout="vertical">
              <a-form-item>
                <a-input v-model:value="doc.name" placeholder="名称" />
              </a-form-item>
              <a-form-item>
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
              <a-form-item>
                <a-input v-model:value="doc.sort" placeholder="顺序" />
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="handlePreviewContent()">
                  <EyeOutlined /> 内容预览
                </a-button>
              </a-form-item>
              <a-form-item>
                <div id="content"></div>
              </a-form-item>
            </a-form>
          </a-col>
        </a-row>
        <a-drawer
          width="900"
          :placement="right"
          :closable="false"
          :visible="drawerVisible"
          @close="onDrawerClose()"
        >
          <div class="wangeditor" :innerHTML="previewHtml"></div>
        </a-drawer>
      </a-layout-content>
    </a-layout>
  </a-layout>
  <a-modal
    title="文档表单"
    v-model:visible="modelVisible"
    :confirm-loading="modelLoading"
    @ok="handleModelOk"
  >
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, createVNode } from "vue";
import axios from "axios";
import { message, Modal } from "ant-design-vue";
import { Tool } from "@/util/tool";
import { useRoute } from "vue-router";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import E from "wangeditor";

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
        slots: { customRender: "name" },
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
      axios.get("/doc/all/" + route.query.ebookId).then((response) => {
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
    const doc = ref();
    doc.value = {};
    const modelVisible = ref(false);
    const modelLoading = ref(false);
    const editor = new E("#content");
    editor.config.zIndex = 0;

    const handleSave = () => {
      modelLoading.value = true;
      doc.value.content = editor.txt.html();
      axios.post("/doc/save", doc.value).then((response) => {
        const data = response.data;
        modelVisible.value = false;
        // data = CommonResp
        if (data.success) {
          // modelLoading.value = false;
          message.success("保存成功！");
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

    // 内容查询
    const handleQueryContent = () => {
      axios.get("/doc/find-content/" + doc.value.id).then((response) => {
        const data = response.data;
        if (data.success) {
          editor.txt.html(data.content);
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 编辑
     */
    const edit = (record: any) => {
      editor.txt.html("");
      doc.value = Tool.copy(record);
      handleQueryContent();

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
      editor.txt.html("");
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

    /**
     * 查找整根树枝
     */
    const getDeleteIDs = (treeSelectData: any, id: any) => {
      // 遍历数组,即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          //如果当前节点就是目标节点
          console.log("disable", node);
          //将目标id放入结果集ids
          // node.disabled = true;
          ids.push(id);

          //遍历所有子节点
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getDeleteIDs(children, children[j].id);
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
      console.log(treeSelectData, id);
      // 遍历数组,即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          //如果当前节点就是目标节点
          //将目标name放入结果集names
          // node.disabled = true;
          deleteNames.push(node.name);

          //遍历所有子节点
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getDeleteNames(children, children[j].id);
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
      ids.length = 0;
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
      // init
      console.log("record.id", record.id);
      deleteNames.length = 0;
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

    // ——————————————————————富文本预览————————————————————————
    const drawerVisible = ref<boolean>(false);
    const previewHtml = ref();
    const handlePreviewContent = () => {
      const html = editor.txt.html();
      previewHtml.value = html; 
      drawerVisible.value = true;
    };
    const onDrawerClose = () => {
      drawerVisible.value = false;
    };

    onMounted(() => {
      handleQuery();
      editor.create();
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
      handleSave,
      doc,

      // 查询
      handleQuery,
      showDeleteConfirm,

      drawerVisible,
      previewHtml,
      handlePreviewContent,
      onDrawerClose,
    };
  },
});
</script>
