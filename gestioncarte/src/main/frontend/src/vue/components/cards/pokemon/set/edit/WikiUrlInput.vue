<template>
    <FormRow>
        <AdvancedFormListInput :path="path">
            <template #label>
                URL <a target="_blank" :href="href" rel="noopener">{{label}}</a>:
            </template>
            <template #default="{path: p}">
                <Row class="w-100">
                    <AdvancedFormInput label="URL" :path="concatPaths([p, 'url'])" />
                    <AdvancedFormInput label="Selecteur CSS" :path="concatPaths([p, 'cssSelector'])" />
                </Row>
            </template>
        </AdvancedFormListInput>
    </FormRow>
</template>

<script lang="ts" setup>
import FormRow from '@components/form/FormRow.vue';
import Row from "@components/grid/Row.vue";
import {AdvancedFormInput, AdvancedFormListInput} from "@components/form/advanced";
import {concatPaths, Path} from "@/path";

interface Props {
    path: Path;
    href: string;
    label: string;
}

defineProps<Props>();
</script>
