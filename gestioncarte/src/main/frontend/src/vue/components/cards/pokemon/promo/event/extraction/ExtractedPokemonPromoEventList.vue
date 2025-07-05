<template>
    <Loading :ready="!isEmpty(events)">
        <div class="container mt-4">
            <PokemonSetSearch v-model="setFilter" />
            <FormButton @click="reload()" color="secondary">Recharger les evenements</FormButton>
        </div>
        <hr />
        <div class="full-screen-container">
            <PromoEventCollapse v-for="event in filteredEvents" :key="event.id" :event="event.event" @remove="remove(event.id)">
                <ExtractedPokemonPromoEvent :modelValue="event" @update:modelValue="v => setEvent(event.id, v)" @save="save(event.id)" />
            </PromoEventCollapse>
        </div>
    </Loading>
</template>

<script lang="ts" setup>
import {RestComposables} from "@/vue/composables/RestComposables";
import {ExtractedPromoCardEventDTO, PokemonSetDTO} from "@/types";
import {isEmpty} from 'lodash';
import {computed, onMounted, ref} from "vue";
import rest from "@/rest";
import {timeout} from "@/retriever";
import {PromoEventCollapse} from "@components/cards/promo";
import PokemonSetSearch from "@components/cards/pokemon/set/search/PokemonSetSearch.vue";
import {useRaise} from "@/alert";
import Loading from "@components/Loading.vue";
import {triggerRef} from "vue-demi";
import FormButton from "@components/form/FormButton.vue";
import {savePromoEvent} from "@components/cards/promo/event/logic";
import ExtractedPokemonPromoEvent
    from "@components/cards/pokemon/promo/event/extraction/ExtractedPokemonPromoEvent.vue";

const raise = useRaise();

const {state: events, refresh} = RestComposables.useRestComposable<ExtractedPromoCardEventDTO[], [], true>("/api/cards/promos/events/extracted?tcg=pok", [], {fullState: true});

const reload = async () => {
    await rest.post("/api/cards/promos/events/extract");
    while ((await refresh()).length === 0) {
        await timeout(500);
    }
}

const setFilter = ref<PokemonSetDTO>();

const filteredEvents = computed(() => !setFilter.value ? events.value : events.value.filter(e => e.setIds?.includes(setFilter.value.id)));
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
