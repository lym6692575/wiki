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
          :data-source="ebooks"
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
          <template #category="{ text, record }">
            <span>
              {{ getCategoryName(record.category1Id) }}
              /
              {{ getCategoryName(record.category2Id) }}
            </span>
          </template>
          <template v-slot:action="{ text, record }">
            <a-space size="small">
              <router-link :to="'/admin/doc?ebookId='+ record.id">
                <a-button type="primary"> 文档管理 </a-button>
              </router-link>
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
    title="电子书表单"
    v-model:visible="modelVisible"
    :confirm-loading="modelLoading"
    @ok="handleModelOk"
  >
    <a-form :model="ebook" :label-col="{ span: 6 }">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover" />
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="分类">
        <a-cascader
          v-model:value="categoryIds"
          :field-names="{ label: 'name', value: 'id', children: 'children' }"
          :options="level1"
        />
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" type="text" />
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
  name: "AdminEbook",
  setup() {
    const ebooks = ref();
    const pagination = ref({
      current: 1,
      pageSize: 4,
      total: 0,
    });
    const loading = ref(false);

    const columns = [
      {
        title: "封面",
        dataindex: "cover",
        slots: { customRender: "cover" },
      },
      {
        title: "名称",
        dataIndex: "name",
      },
      {
        title: "分类",
        slots: { customRender: "category" },
      },
      {
        title: "文档数",
        dataIndex: "docCount",
      },
      {
        title: "阅读数",
        dataIndex: "viewCount",
      },
      {
        title: "点赞数",
        dataIndex: "voteCount",
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
        .get("/ebook/list", {
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
            ebooks.value = data.content.list;
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
    /**
     * 数字 [100,101] 对应: 前端开发/Vue
     */
    const categoryIds = ref();
    const ebook = ref();
    const modelVisible = ref(false);
    const modelLoading = ref(false);

    const handleModelOk = () => {
      modelLoading.value = true;
      ebook.value.category1Id = categoryIds.value[0];
      ebook.value.category2Id = categoryIds.value[1];
      axios.post("/ebook/save", ebook.value).then((response) => {
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
      ebook.value = Tool.copy(record);
      categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id];
    };

    /**
     * 新增
     */
    const add = () => {
      modelVisible.value = true;
      ebook.value = {};
    };

    /**
     * 删除
     */
    const handleDelete = (id: number) => {
      axios.delete("/ebook/delete/" + id).then((response) => {
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

    /**
     * 查询所有分类
     */

    const level1 = ref();

    let categorys: any;

    const handleQueryCategory = () => {
      loading.value = true;
      axios.get("/category/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          categorys = data.content;
          console.log("原始数据:", categorys);

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构:", level1);

          // 加载完分类后,再加载电子书,否则如果分类树加载很慢,则电子书渲染会报错
          handleQuery({
            page: 1,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    };

    const getCategoryName = (cid: number) => {
      let result = "";
      categorys.forEach((item: any) => {
        if (item.id === cid) {
          result = item.name;
        }
      });
      return result;
    };

    onMounted(() => {
      handleQueryCategory();
    });
    return {
      // 表格类
      ebooks,
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
      ebook,

      // 查询
      params,
      handleQuery,

      categoryIds,
      level1,
      handleQueryCategory,
      getCategoryName,
    };
  },
});
</script>
