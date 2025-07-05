<script lang="ts">
import Dropdown from './Dropdown.vue';
import {defineComponent} from "vue"
import Icon from "@components/Icon.vue";

export default defineComponent({
	components: {Icon, Dropdown },
	props: {
		labelClass: String
	}
});
</script>

<template>
	<Dropdown :label-class="labelClass + ' no-after p-0'" class="float-end">
		<template v-slot:label><Icon name="menu" class="icon-24" /></template>
		<template v-slot:default><slot></slot></template>
	</Dropdown>
</template>
