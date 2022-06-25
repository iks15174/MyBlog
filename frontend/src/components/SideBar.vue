<template>
  <div class="col-12 col-md-3 col-xl-2 sidebar">
    <form class="d-flex align-items-center" style="padding: 1rem 15px;">
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
        data-target="#side-nav"
      >
        click here
      </button>
    </form>
    <div class="collapse d-md-block row" id="side-nav">
      <nav>
        <div class="side-item active">
          <router-link
            :to="'/'"
          >
            Category1
          </router-link>
          <ul class="sub-item list-unstyled">
            <li class="active">
              <router-link class="link" :to="'/'">
                Category1's SubItem
              </router-link>
            </li>
            <li>
              <router-link class="link" :to="'/'">
                Category1's SubItem
              </router-link>
            </li>
          </ul>
        </div>
      </nav>
    </div>
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
<style scoped>
a {
  text-decoration: none;
  background-color: transparent;
}
.side-item>a {
    display: block;
    padding: 0.25rem 1.5rem;
    font-weight: 600;
    color: rgba(0,0,0,0.65);
}
.side-item.active>a, .sub-item>li.active>a{
    color: rgba(0,0,0,1);
}
.sub-item>li>a {
    display: block;
    padding: 0.25rem 1.5rem;
    font-size: 90%;
    color: rgba(0,0,0,0.55);
}
.side-item>a:hover, .sub-item>li>a:hover {
    color: rgba(0,0,0,1);
    text-decoration: none;
}
</style>
