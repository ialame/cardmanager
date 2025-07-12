<template>
	<Row class="form-row"><slot></slot></Row>
</template>

<script lang="ts" setup>
import Row from '@components/grid/Row.vue';</script>

<style lang="scss" scoped>
.form-row {
    margin-bottom: 1rem;
}
</style>
