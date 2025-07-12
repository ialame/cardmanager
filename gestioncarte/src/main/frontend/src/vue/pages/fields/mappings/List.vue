<template>
    <div class="container mt-4">
        <FormButton color="secondary" @click="add()"><Icon name="add" /></FormButton>
        <table class="table table-striped">
            <thead>
            <tr>
                <th scope="col">Champ</th>
                <th scope="col">Regex</th>
                <th scope="col">Source</th>
                <th scope="col">valeure</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="mapping in lines" :key="mapping.id">
                <td><input class="input form-control" v-model="mapping.field" @keyup="mapping.changed = true" /></td>
                <td><Checkbox :modelValue="mapping.regex" @update:modelValue="v => { mapping.regex = v; mapping.changed = true; }" :slider="false" /></td>
                <td><input class="input form-control" v-model="mapping.source" @keyup="mapping.changed = true" /></td>
                <td><input class="input form-control" v-model="mapping.value" @keyup="mapping.changed = true" /></td>
                <td>
                    <FormButton v-if="mapping.changed" color="success" class="me-2" @click="save(mapping)"><Icon name="save-outline" /></FormButton>
                    <FormButton color="danger" @click="remove(mapping.id)"><Icon name="trash" /></FormButton>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</template>

<script setup lang="ts">
import {fieldMappingService} from "@/fields/mappings";
import {ref, watch} from "vue";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {FieldMappingDTO} from "@/types";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRaise} from "@/alert";
import Checkbox from "@components/form/Checkbox.vue";

type Line = FieldMappingDTO & {
    changed: boolean;
}

usePageTitle("Dictionaire des champs");

const raise = useRaise();

const lines = ref<Line[]>([]);

watch(fieldMappingService.all, f => {
    for (const mapping of f) {
        const old = lines.value.find(l => l.id === mapping.id);

        if (!old) {
            lines.value.push({
                ...mapping,
                changed: false
            });
        } else {
            old.field = mapping.field;
            old.source = mapping.source;
            old.value = mapping.value;
            old.regex = mapping.regex;
            old.changed = false;
        }
    }
    for (const line of lines.value) {
        if (line.id && !f.find(l => l.id === line.id)) {
            lines.value.splice(lines.value.indexOf(line), 1);
        }
    }
});

const remove = async (id: number) => {
    await fieldMappingService.delete(id);
    raise.success('Le champ a été supprimé');
}

const save = async (mapping: Line) => {
    let copy = {...mapping};

    delete copy.changed;
    await fieldMappingService.save(copy);
    raise.success('Le champ a été sauvegardé');
}

const add = () => {
    lines.value.push({
        id: null,
        field: '',
        source: '',
        value: '',
        regex: true,
        changed: true
    });
}

</script>

<style lang="scss" scoped>
.input:not(:focus):not(:hover) {
    border: none;
    background-color: transparent;
    box-shadow: none;
    margin: 1px;
}
</style>
