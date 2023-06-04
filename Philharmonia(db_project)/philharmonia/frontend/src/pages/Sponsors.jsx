import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {addSponsor, deleteSponsor, getAllSponsors, updateSponsor} from "../services/SponsorService";
import {Box, Button, IconButton, Tooltip} from "@mui/material";
import {Delete, Edit} from "@mui/icons-material";
import {MaterialReactTable} from "material-react-table";
import CreateModal from "../components/UI/CreateModal";

const Sponsors = () => {
    const columns = useMemo(() => [
        {
            header: "ID",
            accessorKey: "id",
            enableEditing: false,
        },
        {
            header: "Имя",
            accessorKey: "name",
        },
        {
            header: "Фамилия",
            accessorKey: "surname",
        }
    ], []);

    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [sponsors, setSponsors] = useState([]);
    const [validationErrors, setValidationErrors] = useState({});


    const refreshData = () => {
        getAllSponsors().then(sponsors => {
            setSponsors(sponsors);
            console.log(sponsors);
        });
    };

    useEffect(() => {
        refreshData();
    }, [])


    const handleSaveRowEdits = async ({exitEditingMode, row, values}) => {
        if (!Object.keys(validationErrors).length) {
            sponsors[row.index] = values;
            console.log(values);
            updateSponsor(values).then(() => refreshData());
            exitEditingMode(); //required to exit editing mode and close modal
        }
    };

    const handleDeleteRow = useCallback(
        (row) => {
            if (!confirm(`Are you sure you want to delete ${row.getValue("name")} ${row.getValue("surname")}`)) {
                return;
            }
            deleteSponsor(row.getValue("id")).then(() => refreshData());
        },
        [],
    );
    const handleCreateNewRow = (values) => {
        addSponsor({...values, id: 0}).then(() => refreshData());
    };

    const handleCancelRowEdits = () => {
        setValidationErrors({});
    };


    return (
        <div>
            <div>
                <MaterialReactTable
                    displayColumnDefOptions={{
                        'mrt-row-actions': {
                            size: 60,
                        },
                    }}
                    columns={columns}
                    data={sponsors}
                    editingMode={"modal"}
                    enableEditing={true}
                    onEditingRowSave={handleSaveRowEdits}
                    onEditingRowCancel={handleCancelRowEdits}
                    renderRowActions={({row, table}) => (
                        <Box sx={{display: 'flex', gap: '1rem'}}>
                            <Tooltip arrow placement="left" title="Edit">
                                <IconButton onClick={() => table.setEditingRow(row)}>
                                    <Edit/>
                                </IconButton>
                            </Tooltip>
                            <Tooltip arrow placement="right" title="Delete">
                                <IconButton color="error" onClick={() => handleDeleteRow(row)}>
                                    <Delete/>
                                </IconButton>
                            </Tooltip>
                        </Box>
                    )}
                    renderTopToolbarCustomActions={() => (
                        <Button
                            color="secondary"
                            onClick={() => setCreateModalOpen(true)}
                            variant="contained"
                        >
                            Добавить организатора
                        </Button>
                    )}
                />
            </div>
            <CreateModal
                columns={columns}
                open={createModalOpen}
                onClose={() => setCreateModalOpen(false)}
                onSubmit={handleCreateNewRow}
            />
        </div>
    )
};

export default Sponsors;
