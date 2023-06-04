import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {
    addContestDistribution,
    deleteContestDistribution,
    getContestsDistribution,
    updateContestDistribution
} from "../services/PerformanceService";
import {MaterialReactTable} from "material-react-table";
import {Box, Button, IconButton, Tooltip} from "@mui/material";
import {Delete, Edit} from "@mui/icons-material";
import CreateModal from "../components/UI/CreateModal";

const PerformancesDistribution = () => {
    const columns = useMemo(() => [
        {
            header: "ID артиста",
            accessorKey: "id.artistId",
            enableEditing: false,
        },
        {
            header: "ID выступления",
            accessorKey: "id.performanceId",
            enableEditing: false,
        }
    ], []);

    const [distribution, setDistribution] = useState([]);
    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [validationErrors, setValidationErrors] = useState({});


    const handleCreateNewRow = (values) => {
        addPermormanceDistribution({...values, id: 0}).then(() => refreshData());
    };

    const handleSaveRowEdits = async ({exitEditingMode, row, values}) => {
        if (!Object.keys(validationErrors).length) {
            distribution[row.index] = values;
            console.log(values);
            updatePerformanceDistribution(values).then(() => refreshData());
            exitEditingMode(); //required to exit editing mode and close modal
        }
    };

    const handleDeleteRow = useCallback(
        (row) => {
            if (!confirm(`Are you sure you want to delete ${row.getValue("name")} ${row.getValue("surname")}`)) {
                return;
            }
            deletePerformanceDistribution(row.getValue("id")).then(() => refreshData());
        },
        [],
    );

    const handleCancelRowEdits = () => {
        setValidationErrors({});
    };

    const refreshData = () => {
        getPerformanceDistribution().then(distribution => {
            setDistribution(distribution);
            console.log(distribution);
        })
    };
    useEffect(() => {
        refreshData();
    }, [])

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
                    data={distribution}
                    onEditingRowSave={handleSaveRowEdits}
                    onEditingRowCancel={handleCancelRowEdits}
                    renderRowActions={({row, table}) => (
                        <Box sx={{display: 'flex', gap: '1rem'}}>
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
                            Добавить связь выступления
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
    );
};

export default PerformancesDistribution;
