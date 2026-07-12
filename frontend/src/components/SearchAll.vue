<template>
  <div class="searchAll">
    <el-popover placement="bottom-start" :visible="popoverVisible" :popper-style="{ marginTop: '-10px' }"
      :width="width" :show-arrow="false">
      <template #reference>
        <el-input :style="{ width: width + 'px', height: height + 'px' }"
          v-model="searchVal" @input="searchFunc" @clear="clearFunc" clearable
          placeholder="请输入搜索内容" class="search-input" v-click-outside="outsideClick">
          <template #suffix>
            <el-icon class="el-input__icon cursor-pointer" @click="searchFunc">
              <Search />
            </el-icon>
          </template>
        </el-input>
      </template>
      <div class="searchRes">
        <div class="item" v-if="searchData.Quotes.length > 0">
          <div class="type">名句</div>
          <div class="con flex flex-col">
            <div class="conItem" @click="conItemClick(item)" v-for="(item, index) in searchData.Quotes" :key="index">
              <span>
                <span v-for="(ele, idx) in item.content" :key="idx" :class="{ searcText: ele === searchVal }">{{ ele }}</span>
                <span class="text-xs ml-2" style="color: #9f9f9f;">— {{ item.poetryName }} ({{ item.writer }})</span>
              </span>
            </div>
          </div>
        </div>
        <div class="item" v-if="searchData.Titles.length > 0">
          <div class="type">标题</div>
          <div class="con flex flex-col">
            <div class="conItem" @click="conItemClick(item)" v-for="(item, index) in searchData.Titles" :key="index">
              <span>
                <span v-for="(ele, idx) in item.title" :key="idx" :class="{ searcText: ele === searchVal }">{{ ele }}</span>
                <span class="text-xs ml-2" style="color: #9f9f9f;">— {{ item.writer }} ({{ item.dynasty }})</span>
              </span>
            </div>
          </div>
        </div>
        <div class="item" v-if="searchData.Authors.length > 0">
          <div class="type">作者</div>
          <div class="con flex flex-col">
            <div class="conItem" @click="writerItemClick(item)" v-for="(item, index) in searchData.Authors" :key="index">
              <span class="searcText">{{ item.name }}<span class="text-xs" style="color: #9f9f9f;">({{ item.dynasty }})</span></span>
            </div>
          </div>
        </div>
        <div class="item" v-if="searchData.Poesy.length > 0">
          <div class="type">诗文</div>
          <div class="con flex flex-col">
            <div class="conItem" @click="conItemClick(item)" v-for="(item, index) in searchData.Poesy" :key="index">
              <span>
                <span v-for="(ele, idx) in item.content" :key="idx" :class="{ searcText: ele === searchVal }">{{ ele }}</span>
                <span class="text-xs ml-2" style="color: #9f9f9f;">— {{ item.title }} ({{ item.writer }})</span>
              </span>
            </div>
          </div>
        </div>
        <div v-if="searchData.Poesy.length === 0 && searchData.Authors.length === 0 && searchData.Quotes.length === 0 && searchData.Titles.length === 0">
          未查询到结果
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ClickOutside as vClickOutside } from 'element-plus'
import { searchAll } from '../api'

const props = defineProps({
  width: { type: Number, default: 600 },
  height: { type: Number, default: 40 },
})

const searchVal = ref('')
const popoverVisible = ref(false)
let debounceTimer: ReturnType<typeof setTimeout> | null = null

const searchData = reactive({
  Quotes: [] as any[],
  Authors: [] as any[],
  Poesy: [] as any[],
  Titles: [] as any[],
})

function searchFunc() {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    if (searchVal.value.length >= 2) {
      getSearchData()
    } else {
      popoverVisible.value = false
      searchData.Quotes = []
      searchData.Authors = []
      searchData.Poesy = []
      searchData.Titles = []
    }
  }, 300)
}

async function getSearchData() {
  try {
    const res = await searchAll(searchVal.value)
    const data = res.data.data

    searchData.Authors = data.Authors || []

    let Poesy = (data.Poesy || []).map((element: any) => {
      let offset = 12
      let sIndex = element.content.indexOf(searchVal.value)
      let firstIndex = sIndex > offset ? sIndex - offset : 0
      let content = element.content.slice(firstIndex, sIndex + offset)
      if (sIndex > offset) content = '... ' + content
      if (sIndex + offset < element.content.length) content = content + ' ...'
      content = content.replace(searchVal.value, '@#$%!' + searchVal.value + '@#$%!')
      element.content = content.split('@#$%!')
      return element
    })
    searchData.Poesy = Poesy

    let Quotes = (data.Quotes || []).map((element: any) => {
      let content = element.content.replace(searchVal.value, '@#$%!' + searchVal.value + '@#$%!')
      element.content = content.split('@#$%!')
      return element
    })
    searchData.Quotes = Quotes

    let Titles = (data.Titles || []).map((element: any) => {
      let title = element.title.replace(searchVal.value, '@#$%!' + searchVal.value + '@#$%!')
      element.title = title.split('@#$%!')
      return element
    })
    searchData.Titles = Titles

    popoverVisible.value = true
  } catch {}
}

function clearFunc() {
  popoverVisible.value = false
}

function outsideClick() {
  popoverVisible.value = false
}

function conItemClick(item: any) {
  window.open(window.location.origin + '/poesy/' + item.id)
}

function writerItemClick(item: any) {
  window.open(window.location.origin + '/authors/' + item.id)
}
</script>

<style scoped>
:deep(.search-input .el-input__wrapper) {
  border: 2px solid #5D6146;
  border-radius: 10px;
  height: 40px;
  overflow: hidden;
  background-color: #FBFAF6;
  box-shadow: none;
}
:deep(.search-input .el-input__wrapper.is-focus) {
  box-shadow: none;
}

:deep(.home-search .search-input .el-input__wrapper) {
  border: 2px solid #c23a2b !important;
  border-radius: 25px !important;
  height: 50px !important;
  background-color: rgba(255, 255, 255, 0.95) !important;
  box-shadow: 0 4px 20px rgba(194, 58, 43, 0.2) !important;
  transition: all 0.3s ease !important;
}
:deep(.home-search .search-input .el-input__wrapper:hover) {
  box-shadow: 0 6px 24px rgba(194, 58, 43, 0.3) !important;
}
:deep(.home-search .search-input .el-input__wrapper.is-focus) {
  border-color: #c23a2b !important;
  box-shadow: 0 8px 32px rgba(194, 58, 43, 0.4) !important;
}
:deep(.home-search .search-input .el-input__inner) {
  font-size: 16px !important;
}

.searchRes {
  max-height: 500px;
  overflow-y: auto;
}
.searchRes > .item {
  width: 100%;
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e9e9e9;
}
.searchRes > .item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}
.searchRes .type {
  margin-right: 6px;
  padding-top: 4px;
  height: 100%;
  flex-shrink: 0;
  font-weight: bold;
}
.searchRes .con {
  flex: 1;
}
.searchRes .con .searcText {
  color: #309d51;
}
.searchRes .con > .conItem {
  padding: 4px;
  margin-bottom: 4px;
  cursor: pointer;
}
.searchRes .con > .conItem:hover {
  background-color: #f0efe2;
}
.searchRes .con > .conItem:last-child {
  margin-bottom: 0;
}
</style>
