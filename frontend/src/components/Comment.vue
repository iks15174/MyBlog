<template>
  <div class="comment-container my-3">
    <div class="comment-input">
      <div class="input-group">
        <textarea
          class="form-control"
          rows="3"
          placeholder="댓글을 입력해주세요."
        ></textarea>
      </div>
      <div class="button-group my-1 text-right">
        <button class="btn btn-primary">등록</button>
      </div>
    </div>
    <div class="commet-list">
      <CommentItem></CommentItem>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue'
import CommentItem from '@/components/CommentItem.vue'

export default defineComponent({
  components: {
    CommentItem
  },
  setup () {
    console.log('comment')
  }
})
</script>
