<template>
    <div class="container">
        <FormRow>
            <PokemonSetSearch v-model="set" />
        </FormRow>
        <FormRow v-if="list.length > 0">
            <hr />
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>title</th>
                        <th>link</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="pattern in list" :key="`${pattern.title}#${pattern.link}`" @click="$emit('setPattern', pattern.title, pattern.link)">
                        <td>{{ pattern.title }}</td>
                        <td>{{ pattern.link }}</td>
                    </tr>
                </tbody>
            </table>
        </FormRow>
    </div>
</template>

<script lang="ts" setup>
import FormRow from "@components/form/FormRow.vue";
import PokemonSetSearch from "@components/cards/pokemon/set/search/PokemonSetSearch.vue";
import {ref} from "vue";
import {PokemonSetDTO, WikiFeaturePattern} from "@/types";
import {computedAsync} from "@vueuse/core";
import rest from "@/rest";

interface Props {
    name: string;
}

interface Emits {
    (e: 'setPattern', title: string, link: string): void;
}

const props = defineProps<Props>();
defineEmits<Emits>();

const set = ref<PokemonSetDTO>();
const list = computedAsync<WikiFeaturePattern[]>(() => {
    if (!set.value || !props.name) {
        return [];
    }
    return rest.get(`/api/cards/pokemon/features/wikis/patterns/${props.name}/${set.value.id}`);
}, []);
</script>
