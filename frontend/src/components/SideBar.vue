<template>
  <div class="col-12 col-md-3 col-xl-2 sidebar">
    <form class="d-flex align-items-center">
        <span style="flex: 1;">
          <input
            type="email"
            class="form-control"
            id="search"
            placeholder="검색어 입력..."
          />
        </span>
        <button
          class="btn d-md-none p-0 ml-3"
          type="button"
          data-toggle="collapse"
          data-target="#categorys"
        >
          click here
        </button>
    </form>
    <nav class="collapse" id="categorys">
      <div class="side-item">
        <router-link class="link" :to="'/'">
          Category1
        </router-link>
        <ul class="nav sub-item">
          <li>
            <router-link class="link" :to="'/'">
              Category1's SubItem
            </router-link>
          </li>
        </ul>
      </div>
    </nav>
  </div>
</template>

<script language="ts">
import { defineComponent } from 'vue'

const {
  VUE_APP_NAVBAR_BG_CSS_COLOR = 'black',
  VUE_APP_NAVBAR_TEXT_CSS_COLOR = 'white'
} = process.env

export default defineComponent({
  props: {
    title: {
      type: String,
      default: ''
    },
    sections: {
      type: Object,
      default: () => ({})
    }
  },
  setup () {
    return {
      VUE_APP_NAVBAR_BG_CSS_COLOR,
      VUE_APP_NAVBAR_TEXT_CSS_COLOR
    }
  }
})
</script>
