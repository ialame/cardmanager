<template>
    <AdvancedFormMergeHeader :labels="['Carte extraite', 'Carte en BDD']">
        <FormButton v-if="!!link" color="bulbapedia" :href="link" :newTab="true" class="mb-1 me-2 form-btn">
            <BulbapediaIcon />
        </FormButton>
    </AdvancedFormMergeHeader>
</template>

<script lang="ts" setup>
import {AdvancedFormMergeHeader} from "@components/form/advanced";
import FormButton from "@components/form/FormButton.vue";
import BulbapediaIcon from "@components/cards/pokemon/BulbapediaIcon.vue";

interface Props {
    link?: string;
}

withDefaults(defineProps<Props>(), {
    link: ''
});
</script>
