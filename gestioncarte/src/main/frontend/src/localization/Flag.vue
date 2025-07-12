<template>
	<img v-if="lang !== undefined" :src="`/svg/flags/squared/${lang}.svg`" class="flag" />
</template>

<script lang="ts" setup>
import {LocalizationCode} from "@/localization";

interface Props {
	lang?: LocalizationCode;
}

defineProps<Props>();
</script>
