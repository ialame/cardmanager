<template>
    <div class="container">
        <CardTagsEditForm v-model="cardTag" @save="save" />
    </div>
</template>

<script lang="ts" setup>
import {computed, ref, watchEffect} from "vue";
import {CardTagDTO} from "@/types";
import {useRoute, useRouter} from "vue-router";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {CardTagsEditForm, cardTagService} from "@components/cards/tags";
import {useRaise} from "@/alert";

const route = useRoute();
const router = useRouter();
const raise = useRaise();

const tagId = computed(() => route.params.tagId as string || '');
const cardTag = ref<CardTagDTO>({
    translations: {},
    id: "",
    type: "",
});

usePageTitle(computed(() => {
    const edit = tagId.value;
    const end = edit ? ` (id: ${tagId.value})` : '';

    return `${edit? 'Modification' : 'Ajout'} de tag${end}`;
}));

const save = async () => {
    let t = await cardTagService.save(cardTag.value);

    raise.success('Tag enregistrée avec succès');
    await router.push(`/cards/tags/${t.id}`);
};
watchEffect(async () => {
    if (tagId.value) {
        cardTag.value = await cardTagService.get(tagId.value);
    }
}, {flush: 'post'});
</script>
