import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const searchVisible = ref(false)
  const searchKeyword = ref('')

  function openSearch(keyword?: string) {
    searchKeyword.value = keyword || ''
    searchVisible.value = true
  }

  function closeSearch() {
    searchVisible.value = false
    searchKeyword.value = ''
  }

  return { searchVisible, searchKeyword, openSearch, closeSearch }
})
