<template>
	<div class="modal fade" ref="modal" tabindex="-1" aria-hidden="true" :class="{'dynamic-modal': dynamic }">
		<div class="modal-dialog modal-dialog-centered" :class="{[`modal-${size}`]: !!size}">
			<div ref="content" class="modal-content" :style="style">
				<div ref="header" class="modal-header" v-if="label || dynamic">
					<h5 class="modal-title"><slot name="label">{{label}}</slot></h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fermer" />
				</div>
				<div ref="body" class="modal-body">
					<slot />
				</div>
			</div>
		</div>
	</div>
    <div class="modal-backdrop show fade" v-if="showBackDrop" @click="hide()" />
</template>

<script lang="ts" setup>
import {Modal} from 'bootstrap';
import {computed, onMounted, onUnmounted, ref} from 'vue';
import {useDraggable, useElementSize} from "@vueuse/core";

const top = 58;
const bottom = 25;

interface Props {
	size?: string;
	label?: string;
    dynamic?: boolean;
}
interface Emits { // TODO use a prop with emit for open/close
    (e: 'toggle', o: boolean): void
    (e: 'open'): void
    (e: 'close'): void
}

const props = withDefaults(defineProps<Props>(), {
	size: '',
	label: '',
    dynamic: false
});
const emit = defineEmits<Emits>();
const modal = ref<HTMLDivElement>();
const bsModal = ref<Modal>();
const show = () => bsModal.value?.show();
const hide = () => bsModal.value?.hide();
const visible = ref(false);

const onShow = (e: Event) => {
    // noinspection JSIncompatibleTypesComparison
    if (e.target === modal.value) {
        visible.value = true;
        emit('toggle', true);
        emit('open');
    }
}
const onHide = (e: Event) => {
    // noinspection JSIncompatibleTypesComparison
    if (e.target === modal.value) {
        visible.value = false;
        emit('toggle', false);
        emit('close');
    }
}
const dismiss = (e: KeyboardEvent) => {
    // noinspection JSIncompatibleTypesComparison
    if (e.target === modal.value && e.key === 'Escape') {
        hide();
        e.preventDefault();
    }
}
onMounted(() => {
	if (modal.value) {
		bsModal.value = new Modal(modal.value, { backdrop: false, keyboard: false });
		modal.value.addEventListener('show.bs.modal', onShow);
		modal.value.addEventListener('hide.bs.modal', onHide);
        modal.value.addEventListener('keydown', dismiss);
	}
});
onUnmounted(() => {
    if (modal.value) {
        modal.value.removeEventListener('show.bs.modal', onShow);
        modal.value.removeEventListener('hide.bs.modal', onHide);
        modal.value.removeEventListener('keydown', onHide);
    }
    bsModal.value?.dispose();
});

const showBackDrop = computed(() => visible.value && !props.dynamic);

const content = ref<HTMLDivElement>();
const header = ref<HTMLDivElement>();
const body = ref<HTMLDivElement>();
const { width, height } = useElementSize(content);
const { x, y, style: draggableStyle } = useDraggable(content,  {
    preventDefault: true,
    stopPropagation: true,
    handle: header,
    initialValue: {x: 300, y: top},
    onStart: () => props.dynamic as any,
    onMove: () => {
        if (x.value < 0) {
            x.value = 0;
        } else if (x.value + width.value > window.innerWidth) {
            x.value = window.innerWidth - width.value;
        }
        if (y.value < top) {
            y.value = top;
        } else if (y.value + height.value > window.innerHeight - bottom) {
            y.value = window.innerHeight - height.value - bottom;
        }
    }
});
const style = computed(() => props.dynamic ? draggableStyle.value : {});

defineExpose({ show, hide, visible });
</script>

<style lang="scss" scoped>
@import "src/variables";
@import "src/mixins";

.dynamic-modal {
    pointer-events: none;

    &:focus-within {
        z-index: 1056;
    }

    > .modal-dialog {
        margin: 0;

        > .modal-content {
            width: auto !important;
            position: fixed !important;
            @include shadow-outline;

            > .modal-header {
                background-color: rgba($primary, 0.05);
                cursor: move;
            }
            > .modal-body {
                resize: both;
                overflow: auto;
                height: 600px;
                width: var(--bs-modal-width);
                @include scroller($secondary, $white);
            }
        }
    }
}
</style>
