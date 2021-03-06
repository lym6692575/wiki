<template>
  <a-layout-content
    :style="{
      background: '#fff',
      padding: '24px',
      margin: 0,
      minHeight: '280px',
    }"
  >
    <h3 v-if="level1.length === 0">对不起，找不到相关文档！</h3>
    <a-row>
      <a-col :span="6">
        <a-tree
          showLine
          v-if="level1.length > 0"
          :tree-data="level1"
          @select="onSelect"
          :replaceFields="{
            title: 'name',
            key: 'id',
            value: 'id',
          }"
          :defaultExpandAll="true"
        >
        </a-tree>
      </a-col>
      <a-col :span="18">
        <div class="wangeditor" :innerHTML="html"></div>
      </a-col>
    </a-row>
  </a-layout-content>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, createVNode } from "vue";
import axios from "axios";
import { message, Modal } from "ant-design-vue";
import { Tool } from "@/util/tool";
import { useRoute } from "vue-router";

export default defineComponent({
  name: "Doc",
  setup() {
    const route = useRoute();
    const docs = ref();
    const html = ref();

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
    level1.value = [];
    // 数据查询
    const handleQuery = () => {
      axios.get("/doc/all/" + route.query.ebookId).then((response) => {
        const data = response.data;
        if (data.success) {
          docs.value = data.content;
          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
          console.log("树形结构:", level1);
          // 重置分页按钮
        } else {
          message.error(data.message);
        }
      });
    };
    // 内容查询
    const handleQueryContent = (id: number) => {
      axios.get("/doc/find-content/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          html.value = data.content;
        } else {
          message.error(data.message);
        }
      });
    };

    const onSelect = (selectedKeys: any, info: any) => {
      console.log("selected", selectedKeys, info);
      if (Tool.isNotEmpty(selectedKeys)) {
        handleQueryContent(selectedKeys[0]);
      }
    };

    onMounted(() => {
      handleQuery();
    });
    return {
      level1,
      html,
      onSelect,
    };
  },
});
</script>

<style scoped>
/* wangeditor样式  */
/* table 样式 */
.wangtditor table {
  border-top: 1px solid #ccc;
  border-left: 1px solid #ccc;
}
.wangtditor table td,
.wangtditor table th {
  border-bottom: 1px solid #ccc;
  border-right: 1px solid #ccc;
  padding: 3px 5px;
}
.wangtditor table th {
  border-bottom: 2px solid #ccc;
  text-align: center;
}

/* blockquote 样式 */
.wangtditor blockquote {
  display: block;
  border-left: 8px solid #d0e5f2;
  padding: 5px 10px;
  margin: 10px 0;
  line-height: 1.4;
  font-size: 100%;
  background-color: #f1f1f1;
}
/* code 样式 */
.wangtditor code {
  display: inline-block;
  *display: inline;
  *zoom: 1;
  background-color: #f1f1f1;
  border-radius: 3px;
  padding: 3px 5px;
  margin: 0 3px;
}
.wangtditor pre code {
  display: block;
}

/* ul ol 样式 */
.wangtditor ul,
ol {
  margin: 10px 0 10px 20px;
}

.wangeditor blockquote p {
  font-family: "YouYuan";
  margin: 20px 10px !important;
  font-size: 16px !important;
  font-weight: 600;
}
</style>
