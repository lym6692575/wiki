<template>
  <a-layout>
    <a-layout>
      <a-layout-sider width="200" style="background: #fff">
        <a-menu
          mode="inline"
          v-model:selectedKeys="selectedKeys2"
          v-model:openKeys="openKeys"
          :style="{ height: '100%', borderRight: 0 }"
        >
          <a-menu-item key="welcome">
            <router-link :to="'/'">
              <span>欢迎</span>
            </router-link>
          </a-menu-item>
          <a-sub-menu v-for="item in level1" :key="item.id">
            <template #title>
              <span>
                <user-outlined />
                {{ item.name }}
              </span>
            </template>
            <a-menu-item v-for="child in item.children" :key="child.id">
              <span>
                <user-outlined />
                {{ child.name }}
              </span>
            </a-menu-item>
          </a-sub-menu>
        </a-menu>
      </a-layout-sider>
      <a-layout>
        <a-layout-content
          :style="{
            background: '#fff',
            padding: '24px',
            margin: 0,
            minHeight: '280px',
          }"
        >
          <a-list
            item-layout="vertical"
            size="large"
            :data-source="ebooks"
            :grid="{ gutter: 20, column: 3 }"
          >
            <template #renderItem="{ item }">
              <a-list-item key="item.name">
                <template #actions>
                  <span v-for="{ type, text } in actions" :key="type">
                    <component v-bind:is="type" style="margin-right: 8px" />
                    {{ text }}
                  </span>
                </template>
                <a-list-item-meta :description="item.description">
                  <template #title>
                    <a :href="item.href">{{ item.name }}</a>
                  </template>
                  <template #avatar><a-avatar :src="item.cover" /></template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </a-layout-content>
      </a-layout>
    </a-layout>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from "vue";
import {
  StarOutlined,
  LikeOutlined,
  MessageOutlined,
} from "@ant-design/icons-vue";
import axios from "axios";
import { message } from "ant-design-vue";
import { Tool } from "@/util/tool.ts";

export default defineComponent({
  name: "Home",
  components: {
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
  },
  setup() {
    const ebooks = ref();
    onMounted(() => {
      axios
        .get("/ebook/list", { params: { page: 1, size: 1000 } })
        .then((response) => {
          const data = response.data;
          ebooks.value = data.content.list;
          // ebooks1.books = data.content;
        });
    });
    const pagination = {
      onChange: (page: number) => {
        console.log(page);
      },
      pageSize: 3,
    };
    const actions: Record<string, string>[] = [
      { type: "StarOutlined", text: "156" },
      { type: "LikeOutlined", text: "156" },
      { type: "MessageOutlined", text: "2" },
    ];

    /**
     * 一级分类树，children属性就是二级分类
     * [{
     *  id: "",
     *  name: "",
     *  children: [{
     *    id: "",
     *    name: "",
     *  }]
     * }]
     */

    // 一级分类树,children属性就是二级分类
    const level1 = ref();
    // 数据查询
    const handleQuery = () => {
      axios.get("/category/all").then((response) => {
        const data = response.data;
        if (data.success) {
          const categorys = data.content;
          console.log("原始数据:", categorys);
          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构:", level1);
          // 重置分页按钮
        } else {
          message.error(data.message);
        }
      });
    };
    onMounted(() => {
      handleQuery();
    });
    return {
      ebooks,
      pagination,
      actions,
      level1,
    };
  },
});
</script>

<style scoped>
.ant-avatar {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}
</style>