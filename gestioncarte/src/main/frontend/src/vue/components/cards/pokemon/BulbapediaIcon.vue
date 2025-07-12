<template>
    <Icon src="/svg/bulbapedia.svg" class="bulbapedia-icon" />
</template>

<script lang="ts" setup>
import Icon from "@components/Icon.vue";</script>

<style lang="scss" scoped>
@import 'src/variables';

$btn-classes: ':not(.btn-bulbapedia)';

@each $color, $value in $theme-colors {
    $btn-classes: $btn-classes + ':not(.btn-#{$color})';
}

#{$btn-classes} > .bulbapedia-icon {
    color: $bulbapedia-green !important;
}
</style>
