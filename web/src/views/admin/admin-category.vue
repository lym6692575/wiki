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
            <a-input v-model:value="params.name" placeholder="名称"> </a-input>
          </a-form-item>
          <a-form-item>
            <a-space size="small">
              <a-button type="primary" @click="handleQuery(params)">
                搜索
              </a-button>
              <a-button type="primary" @click="add()"> 新增 </a-button>
            </a-space>
          </a-form-item>
        </a-form>
        <a-table
          :columns="columns"
          :row-key="(record) => record.id"
          :data-source="categorys"
          :loading="loading"
          :pagination="pagination"
          @change="handleTableChange"
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
                @confirm="handleDelete(record.id)"
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
    title="分类表单"
    v-model:visible="modelVisible"
    :confirm-loading="modelLoading"
    @ok="handleModelOk"
  >
    <a-form :model="category" :label-col="{ span: 6 }">
      <a-form-item label="名称">
        <a-input v-model:value="category.name" />
      </a-form-item>
      <a-form-item label="父分类">
        <a-input v-model:value="category.parent" />
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from "vue";
import axios from "axios";
import { message } from "ant-design-vue";
import { Tool } from "@/util/tool";

export default defineComponent({
  name: "AdminCategory",
  setup() {
    const categorys = ref();
    const pagination = ref({
      current: 1,
      pageSize: 4,
      total: 0,
    });
    const loading = ref(false);
    const columns = [
      {
        title: "名称",
        dataIndex: "name",
      },
      {
        title: "父分类",
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
    // 查询参数
    const params = ref({
      page: pagination.value.current,
      size: pagination.value.pageSize,
      name: "",
    });

    // 数据查询
    const handleQuery = (params: any) => {
      loading.value = true;
      axios
        .get("/category/list", {
          params: {
            page: params.page,
            size: params.size,
            name: params.name,
          },
        })
        .then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            categorys.value = data.content.list;
            // 重置分页按钮
            pagination.value.current = params.page;
            pagination.value.total = data.content.total;
          } else {
            message.error(data.message);
          }
        });
    };
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页都有啥:" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize,
      });
    };

    // 表单
    const category = ref({});
    const modelVisible = ref(false);
    const modelLoading = ref(false);

    const handleModelOk = () => {
      modelLoading.value = true;
      axios.post("/category/save", category.value).then((response) => {
        const data = response.data;
        modelVisible.value = false;
        // data = CommonResp
        if (data.success) {
          modelLoading.value = false;
          // 重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message);
          modelLoading.value = false;
        }
      });
    };
    /**
     * 编辑
     */
    const edit = (record: any) => {
      modelVisible.value = true;
      category.value = Tool.copy(record);
    };

    /**
     * 新增
     */
    const add = () => {
      modelVisible.value = true;
      category.value = {};
    };

    /**
     * 删除
     */
    const handleDelete = (id: number) => {
      axios.delete("/category/delete/" + id).then((response) => {
        const data = response.data;
        // data = CommonResp
        if (data.success) {
          // 重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        }
      });
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
      });
    });
    return {
      // 表格类
      categorys,
      pagination,
      columns,
      loading,
      handleTableChange,

      // 功能
      edit,
      add,
      handleDelete,

      // 表单类
      modelVisible,
      modelLoading,
      handleModelOk,
      category,

      // 查询
      params,
      handleQuery,
    };
  },
});
</script>
