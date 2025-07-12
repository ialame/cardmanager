<template>
    <FormRow>
        <AdvancedFormListInput :path="path" :alignKey="`${pathToString(path)}-${localization}`" label="Promos" #default="{ path: promoPath }" :initialValue="{localization: localization}" :filter="v => v.localization === localization">
            <PromoInput :tcg="tcg" :path="promoPath" />
        </AdvancedFormListInput>
    </FormRow>
</template>

<script setup lang="ts">

import {Path, pathEndsWith, pathStartsWith, pathToString} from "@/path";
import {AdvancedFormListInput, useAdvancedFormInput, watchAdvancedForm} from "@components/form/advanced";
import FormRow from "@components/form/FormRow.vue";
import PromoInput from "./PromoInput.vue";
import {LocalizationCode} from "@/localization";
import {PromoCardDTO} from "@/types";
import {get, toPath} from "lodash";

interface Props {
    path: Path;
    tcg: string;
    localization: LocalizationCode;
}

const props = defineProps<Props>();

const { value } = useAdvancedFormInput<any, PromoCardDTO[]>(() => props.path);

watchAdvancedForm<any>((data, path) => {
    if (!pathStartsWith(path, props.path) || !pathEndsWith(path, 'used')) {
        return;
    }

    const p = toPath(path);
    const copy: PromoCardDTO[] = get(data, props.path) ?? [];
    const index = parseInt(p[p.length - 2]);

    if (!copy) {
        return;
    }
    copy.forEach((promo, i) => {
        if (i !== index) {
            promo.used = false;
        }
    });
    value.value = copy;
});

</script>
