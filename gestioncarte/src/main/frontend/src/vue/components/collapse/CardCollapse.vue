<template>
	<RequiredCollapse class="card border-primary w-100 p-0">
		<template v-slot:toggle>
			<div class="card-header">
				<FormButton color="link" class="no-focus" @click="toggle()"><slot name="label">{{label}}</slot></FormButton>
				<slot name="header"></slot>
			</div>
		</template>
		<template v-slot:default="{required: r}">
			<div class="card-body container">
				<slot :required="r"></slot>
			</div>
		</template>
	</RequiredCollapse>
</template>

<script lang="ts" setup>
import RequiredCollapse from './RequiredCollapse.vue';
import FormButton from '../form/FormButton.vue';

interface Props { 
	label?: string;
}

withDefaults(defineProps<Props>(),  {
	label: ''
});

</script>
