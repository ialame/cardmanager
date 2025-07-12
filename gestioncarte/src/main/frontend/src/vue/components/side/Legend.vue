<template>
	<FormButton color="info" class="round-btn shadow-lg pt-2 ms-2 mb-2" data-bs-toggle="collapse" data-bs-target="#legend" aria-expanded="false" aria-controls="legend">
		<Icon name="list" class="icon-32"></Icon>
	</FormButton>
	<div class="card collapse" id="legend">
		<ul class="legend mt-2" v-once>
			<li v-for="value in values" :key="value.text">
				<span v-if="'iconName' in value" class="border-0"><Icon :name="getLegendName(value)" :class="getLegendColor(value)" class="ms-2 icon-16"/></span>
				<span v-else-if="'iconSrc' in value" class="border-0"><Icon :src="getLegendSrc(value)" :class="getLegendColor(value)" class="ms-2 icon-16"/></span>
				<span v-else :class="value.color"></span> 
				{{value.text}}
			</li>
		</ul>
	</div>
</template>

<script lang="ts" setup>
import {getLegendColor, getLegendName, getLegendSrc, LegendType} from '@/types';
import FormButton from '@components/form/FormButton.vue';
import Icon from "@components/Icon.vue";

interface Props {
	values: LegendType[];
}

withDefaults(defineProps<Props>(), {
    values: () => []
});
</script>

<style lang="scss">


#legend {
	position: absolute;
	left: auto;
	right: 100%;
}
.legend {
    list-style: none;
	padding-right: calc(1rem + 37px);
	width: 100%;
}
.legend li {
    margin-right: 10px;
    width: 100%;
    clear: both;
    text-decoration: none;
    white-space: nowrap;
}

.legend span {
    border: 1px solid #ccc;
    float: left;
    width: 32px;
    height: 18px;
    margin-top: 2px;
    margin-right: 5px;
}
</style>
