<template>
    <Loading :ready="!isEmpty(events)">
        <div class="container mt-4">
            <FormButton @click="reload()" color="secondary">Recharger les evenements</FormButton>
        </div>
        <hr />
        <div class="full-screen-container">
            <PromoEventCollapse v-for="event in events" :key="event.id" :event="event.event" @remove="remove(event.id)">
                <ExtractedOnePiecePromoEvent :modelValue="event" @update:modelValue="v => setEvent(event.id, v)" @save="save(event.id)" />
            </PromoEventCollapse>
        </div>
    </Loading>
</template>

<script lang="ts" setup>
import {RestComposables} from "@/vue/composables/RestComposables";
import {ExtractedPromoCardEventDTO} from "@/types";
import {isEmpty} from 'lodash';
import {onMounted} from "vue";
import rest from "@/rest";
import {timeout} from "@/retriever";
import {PromoEventCollapse} from "@components/cards/promo";
import {useRaise} from "@/alert";
import Loading from "@components/Loading.vue";
import {triggerRef} from "vue-demi";
import FormButton from "@components/form/FormButton.vue";
import {savePromoEvent} from "@components/cards/promo/event/logic";
import ExtractedOnePiecePromoEvent
    from "@components/cards/onepiece/promo/event/extraction/ExtractedOnePiecePromoEvent.vue";

const raise = useRaise();

const {state: events, refresh} = RestComposables.useRestComposable<ExtractedPromoCardEventDTO[], [], true>("/api/cards/promos/events/extracted?tcg=onp", [], {fullState: true});

const reload = async () => {
    await rest.post("/api/cards/promos/events/extract");
    while ((await refresh()).length === 0) {
        await timeout(500);
    }
}
const setEvent = (id: string, event: ExtractedPromoCardEventDTO) => {
    const index = events.value.findIndex(e => e.id === id);

    events.value.splice(index, 1, event);
    triggerRef(events);
}
const save = async (id: string) => {
    const editEvent = events.value.find(e => e.id === id);

    if (!editEvent) {
        return;
    }

    await savePromoEvent(editEvent);

    events.value.splice(events.value.findIndex(e => e.id === editEvent.id), 1);
    triggerRef(events);
    raise.success("L'évènement a été sauvegardé");
}
const remove = async (id: string) => {
    await rest.delete(`/api/cards/promos/events/extracted/${id}`);
    events.value.splice(events.value.findIndex(e => e.id === id), 1);
    triggerRef(events);
    raise.success("L'évènement a été supprimé");
}

onMounted(async () => {
    if ((await refresh()).length > 0) {
        return;
    }
    await reload();
});

</script>

<style lang="scss" scoped>
.btn.form-btn {
  padding: 1px 5px;
  width: 28px;
  height: 28px;
  border-radius: 0.375rem !important;
}
</style>
