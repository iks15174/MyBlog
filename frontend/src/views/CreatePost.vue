<template>
  <div class="create-post-container">
    <PatchMeta :title="title" />
    <div class="input-group">
      <input class="form-control" placeholder="제목을 입력하세요." />
    </div>
    <div class="create-content my-1">
      <div class="buttons text-right">
        <button
          class="btn-sm btn-light"
          :class="{ active: editMode }"
          @click="changeEditMode(true)"
        >
          편집
        </button>
        <button
          class="btn-sm btn-light"
          :class="{ active: !editMode }"
          @click="changeEditMode(false)"
        >
          미리보기
        </button>
      </div>
      <div class="input-group">
        <textarea
          v-if="editMode"
          class="form-control"
          rows="22"
          placeholder="내용을 입력해주세요."
          v-model="content"
        ></textarea>
        <span v-else v-html="mdContent" />
      </div>
    </div>
    <div class="create-foot fixed-bottom text-right bg-light py-3 px-2">
      <button class="btn btn-dark mx-1">임시저장</button>
      <button class="btn btn-dark mx-1">저장</button>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, ref } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import emoji from 'markdown-it-emoji'
import PatchMeta from '@/components/PatchMeta.vue'

export default defineComponent({
  components: {
    PatchMeta
  },
  setup () {
    const markDownIt = new MarkdownIt({
      html: true,
      linkify: true,
      typographer: true,
      highlight: (str: string, lang: string) => {
        if (lang && hljs.getLanguage(lang)) {
          try {
            return (
              '<pre class="hljs"><code>' +
              hljs.highlight(str, { language: lang, ignoreIllegals: true })
                .value +
              '</code></pre>'
            )
          } catch (__) {}
        }

        return ''
      }
    }).use(emoji)
    const editMode = ref(true)
    const content = ref('')
    const mdContent = ref('')
    const changeEditMode = (isEditMode: boolean) => {
      editMode.value = isEditMode
      if (!editMode.value) {
        mdContent.value = markDownIt.render(content.value)
      }
    }
    return {
      editMode,
      content,
      mdContent,
      changeEditMode
    }
  }
})
</script>
