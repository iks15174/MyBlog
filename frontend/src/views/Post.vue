<template>
  <div>
    <PatchMeta :title="title" />
    <div class="container my-4 my-md-5">
      <!-- <span
        class="markdown-body"
        :style="
          `background-color: ${VUE_APP_MAIN_BG_CSS_COLOR}; color: ${VUE_APP_MAIN_TEXT_CSS_COLOR};`
        "
        v-html="postHtml"
      /> -->
      <span>{{ postHtml }}</span>
      <Comment />
      <button
        type="button"
        :style="`color: ${VUE_APP_MAIN_TEXT_CSS_COLOR};`"
        class="border btn mt-4"
        @click="hasHistory() ? router.go(-1) : router.push('/')"
      >
        &laquo; Back
      </button>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, inject } from 'vue'
import { onBeforeRouteUpdate } from 'vue-router'
import router from '@/router'
import MarkdownIt from 'markdown-it'
import emoji from 'markdown-it-emoji'
import { PostIndex } from '@/types/PostIndex'
import PatchMeta from '@/components/PatchMeta.vue'
import Comment from '@/components/Comment.vue'

const {
  VUE_APP_MAIN_BG_CSS_COLOR = 'white',
  VUE_APP_MAIN_TEXT_CSS_COLOR = 'black'
} = process.env

const markDownIt = new MarkdownIt({ html: true }).use(emoji)

export default defineComponent({
  components: {
    PatchMeta,
    Comment
  },
  props: {
    section: {
      type: String,
      default: ''
    },
    id: {
      type: String,
      default: ''
    }
  },
  setup (props) {
    // Fetch Post markdown and compile it to html
    const postsIndex: PostIndex[] = inject<PostIndex[]>('postsIndex', [])
    const posts = postsIndex.find(({ id }) => id === props.id)
    let postHtml
    let title
    if (posts && 'description' in posts) postHtml = posts.description
    else postHtml = 'TEST Description'

    // Patch page title
    if (posts && 'title' in posts) title = posts.description
    else title = 'TEST title'

    // Back button helper
    const hasHistory = () => window.history?.length > 2

    return {
      hasHistory,
      postHtml,
      router,
      title,
      VUE_APP_MAIN_BG_CSS_COLOR,
      VUE_APP_MAIN_TEXT_CSS_COLOR
    }
  }
})
</script>
