<template>
    <div class="container border border-2 rounded mt-4">
		<Row>
			<Column>{{name}}</Column>
			<Column size="sm">
				<FormButton color="info" @click="run" class="float-end m-1"><Icon name="play" /></FormButton>
			</Column>
		</Row>
	</div>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import FormButton from '@components/form/FormButton.vue';
import Column from '@components/grid/Column.vue';
import Row from '@components/grid/Row.vue';
import Icon from "@components/Icon.vue";
import {useRaise} from "@/alert";

interface Props {
	name: string;
}

const props = defineProps<Props>();
const raise = useRaise();

const run = () => {
	if (props.name) {
		rest.post("/api/scheduler/tasks/" + encodeURI(props.name) + "/run", {
			success: () => raise.success("La tache a été démaré.")
		});
	}
};
defineExpose({ run });
</script>
